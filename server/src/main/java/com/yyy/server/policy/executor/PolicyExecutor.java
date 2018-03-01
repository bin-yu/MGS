package com.yyy.server.policy.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private boolean isFullScheduled = false;


    @PostConstruct
    public void start() {
        logger.info("Starting policy executor...intervalHours=" + intervalHours);

        executorPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "PolicyExecutor");
            }

        });
        executorPool.scheduleWithFixedDelay(() -> execute(null), 0, intervalHours, TimeUnit.HOURS);
    }

    public synchronized void runNow() {
        if (isFullScheduled) {
            logger.info("Full Policy Execution is currently running, ignore runNow operation!");
        } else {
            isFullScheduled = true;
            executorPool.execute(() -> execute(null));
            logger.info("Full Policy Execution scheduled!");
        }
    }

    public void runOnWorker(Worker worker) {
        executorPool.execute(() -> execute(worker));
        logger.info("Policy runNow scheduled for worker " + worker);
    }

    protected void execute(Worker worker) {
        try {
            Iterable<Worker> workerIter = null;
            if (worker == null) {
                workerIter = workerRepo.findAll();
                logger.info("Running policy against all the workers!");
            } else {
                worker = workerRepo.findOne(worker.getId());
                logger.info("Running policy against worker:" + worker);
                workerIter = Collections.singletonList(worker);
            }
            executePolicies(workerIter);

            //upload blacklist cards to door systems
            Map<Long, List<Card>> doorToCards = new HashMap<Long, List<Card>>();
            if (worker == null) {
                findCardsToUpdate(doorToCards);
            } else {
                if (worker.getInBlackList()) {
                    addWorkerCardsToMap(doorToCards, worker);
                }
            }
            uploadBlackListCardsToDoorSystems(doorToCards);
        } catch (Throwable t) {
            logger.warn("Failed to execute policy!", t);
        } finally {
            isFullScheduled = false;
        }
    }


    private void uploadBlackListCardsToDoorSystems(Map<Long, List<Card>> doorToCards) {
        logger.info("Start uploading blacklist...size=" + doorToCards.size());
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


    private void findCardsToUpdate(Map<Long, List<Card>> doorToCards) {
        List<Worker> workers = workerRepo.findByInBlackList(true);
        if (workers == null || workers.size() == 0) {
            logger.info("No cards to update because there is no worker in blacklist.");
            return;
        }
        workers.forEach(worker -> {
            addWorkerCardsToMap(doorToCards, worker);
        });
    }

    private void addWorkerCardsToMap(Map<Long, List<Card>> doorToCards, Worker worker) {
        List<Card> cards = cardRepo.findByWorker(worker);
        cards.forEach(card -> {
            if (card.getInBlackList()) {
                logger.info("Ignore updating this card to blacklist because it has been updated:" + card);
            } else {
                addCardToMap(doorToCards, card);
            }
        });
    }

    private void addCardToMap(Map<Long, List<Card>> doorToCards, Card card) {
        logger.info("Adding this card to blacklist:" + card);
        List<Card> cardList = doorToCards.get(card.getDoorId());
        if (cardList == null) {
            cardList = new ArrayList<Card>();
            doorToCards.put(card.getDoorId(), cardList);
        }
        cardList.add(card);
    }


    private void executePolicies(Iterable<Worker> workerIter) throws IOException {
        logger.info("Start Policy Executing...");
        List<Policy> policies = policyRepo.findAll();
        if (policies == null || policies.size() == 0) {
            logger.info("Ignore executing policies because the policy cnt is 0!");
        }
        logger.info("Policy cnt = " + policies.size());
        executePoliciesOnWorkers(policies, workerIter);
        logger.info("End Policy Executing...");
    }

    private void executePoliciesOnWorkers(List<Policy> policies, Iterable<Worker> workerIter) {
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
    }

}
