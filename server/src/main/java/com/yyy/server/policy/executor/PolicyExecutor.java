package com.yyy.server.policy.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yyy.server.card.repo.Card;
import com.yyy.server.card.repo.CardRepo;
import com.yyy.server.door.facade.DoorSystem;
import com.yyy.server.door.facade.DoorSystemFactory;
import com.yyy.server.door.repo.Door;
import com.yyy.server.door.repo.DoorRepo;
import com.yyy.server.policy.repo.Policy;
import com.yyy.server.policy.repo.PolicyRepo;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.worker.repo.WorkerRepo;

@Service
public class PolicyExecutor {
    private Logger logger = LoggerFactory.getLogger(PolicyExecutor.class);
    @Autowired
    private PolicyRepo policyRepo;
    @Autowired
    private WorkerRepo workerRepo;

    @Autowired
    private CardRepo cardRepo;

    @Autowired
    private DoorRepo doorRepo;
    @Value("${mgs.policy.executor.interval-hours}")
    private int intervalHours;
    private ScheduledExecutorService executorPool;
    private boolean isRunning = false;


    @PostConstruct
    public void start() {
        logger.info("Starting policy executor...intervalHours=" + intervalHours);

        executorPool = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "PolicyExecutor");
            }

        });
        executorPool.scheduleWithFixedDelay(() -> execute(), 0, intervalHours, TimeUnit.HOURS);
    }

    public void runNow() {
        if (isRunning) {
            logger.info("Policy is currently running, ignore runNow operation!");
        } else {
            executorPool.execute(() -> execute());
            logger.info("Policy runNow scheduled!");
        }
    }

    protected void execute() {
        try {
            isRunning = true;
            executePolicies();
            uploadBlackListCardsToDoorSystems();
        } catch (Throwable t) {
            logger.warn("Failed to execute policy!", t);
        } finally {
            isRunning = false;
        }
    }


    private void uploadBlackListCardsToDoorSystems() {
        logger.info("Start uploading blacklist...");
        Map<Long, List<Card>> doorToCards = findCardsToUpdate();
        doorToCards.forEach((doorId, cards) -> {
            try {
                DeleteCardsFromDoor(doorId, cards);
                setCardInBlackListInDb(cards);

            } catch (IOException e) {
                logger.warn("Failed to delete cards from door" + doorId, e);
            }
        });
        logger.info("End uploading blacklist...");

    }


    private void setCardInBlackListInDb(List<Card> cards) {
        logger.info("Updating db to move cards into blacklist:" + cards);
        cards.forEach(card -> {
            card.setInBlackList(true);
        });
        cardRepo.save(cards);
    }


    private void DeleteCardsFromDoor(Long doorId, List<Card> cards) throws IOException {
        Door door = doorRepo.findOne(doorId);
        if (door == null) {
            logger.warn("Door not found, so ignore the uploading : " + doorId);
        }
        DoorSystem doorSys = DoorSystemFactory.createInstance(door);
        long[] cardNos = new long[cards.size()];
        for (int i = 0; i < cardNos.length; i++) {
            cardNos[i] = cards.get(i).getCardNo();
        }
        logger.info("Deleting cards " + cards + " from door " + door + "...");
        doorSys.deleteCard(cardNos);
    }


    private Map<Long, List<Card>> findCardsToUpdate() {
        Map<Long, List<Card>> doorToCards = new HashMap<Long, List<Card>>();
        List<Worker> workers = workerRepo.findByInBlackList(true);
        if (workers == null || workers.size() == 0) {
            logger.info("Ignore uploading blacklist because there is no worker in blacklist.");
            return doorToCards;
        }
        workers.forEach(worker -> {
            List<Card> cards = cardRepo.findByWorker(worker);
            cards.forEach(card -> {
                if (card.getInBlackList()) {
                    logger.info("Ignore updating this card to blacklist because it has been updated:" + card);
                } else {
                    logger.info("Adding this card to blacklist:" + card);
                    List<Card> cardList = doorToCards.get(card.getDoorId());
                    if (cardList == null) {
                        cardList = new ArrayList<Card>();
                        doorToCards.put(card.getDoorId(), cardList);
                    }
                    cardList.add(card);
                }
            });
        });
        return doorToCards;
    }


    private void executePolicies() throws IOException {
        logger.info("Start Policy Executing...");
        List<Policy> policies = policyRepo.findAll();
        if (policies == null || policies.size() == 0) {
            logger.info("Ignore executing policies because the policy cnt is 0!");
        }
        logger.info("Policy cnt = " + policies.size());
        Iterable<Worker> workerIter = workerRepo.findAll();
        workerIter.forEach(worker -> {
            policies.forEach(policy -> {
                if (policy.getCondition().isSatisfiedBy(worker)) {
                    if (policy.getAction().apply(worker)) {
                        workerRepo.save(worker);
                        logger.info("Worker updated:" + worker.getId());
                    }
                }
            });
        });
        logger.info("End Policy Executing...");
    }

}
