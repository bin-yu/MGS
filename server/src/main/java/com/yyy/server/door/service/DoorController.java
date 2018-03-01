package com.yyy.server.door.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.card.repo.Card;
import com.yyy.server.card.repo.Card.CardKey;
import com.yyy.server.card.repo.CardRepo;
import com.yyy.server.door.facade.CardAreaStatus;
import com.yyy.server.door.facade.CardData;
import com.yyy.server.door.facade.DoorSystem;
import com.yyy.server.door.facade.DoorSystemFactory;
import com.yyy.server.door.repo.Door;
import com.yyy.server.door.repo.DoorRepo;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.worker.repo.WorkerRepo;

@RestController
@RequestMapping({"/doors"})
public class DoorController {
    private Logger logger = LoggerFactory.getLogger(DoorController.class);
    @Autowired
    private DoorRepo repo;
    @Autowired
    private CardRepo cardRepo;
    @Autowired
    private WorkerRepo workerRepo;

    @GetMapping
    public Page<Door> getDoors(Pageable pageable) throws Exception {
        Page<Door> doors = repo.findAll(pageable);
        logger.info("getDoors: returned Door count:" + doors.getNumberOfElements());
        return doors;
    }

    @GetMapping("/{id}")
    public Door getDoor(@PathVariable Long id) {
        return repo.findOne(id);
    }

    @PostMapping()
    public Door addDoor(@RequestBody Door Door) {
        return repo.save(Door);
    }

    @PutMapping("/{id}")
    public Door updateDoor(@PathVariable Long id, @RequestBody Door Door) {
        if (!id.equals(Door.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        return repo.save(Door);
    }

    @DeleteMapping("/{id}")
    public void deleteDoor(@PathVariable Long id) {
        repo.delete(id);
    }

    @GetMapping("/{id}/version")
    public Version getVersion(@PathVariable Long id) throws IOException {
        DoorSystem doorSys = getDoorSystem(id);
        return new Version(doorSys.getVersion());
    }

    @GetMapping("/{id}/cardAreaStatus")
    public CardAreaStatus getCardAreaStatus(@PathVariable Long id) throws IOException {
        DoorSystem doorSys = getDoorSystem(id);
        return doorSys.readCardAreaStatus();
    }

    @GetMapping("/{did}/cards")
    public Page<Card> getCards(@PathVariable Long did, Pageable pageable) throws IOException {
        return cardRepo.findByDoorId(did, pageable);
    }

    @GetMapping("/{did}/cards/{cid}/cardData")
    public CardData readCard(@PathVariable Long did, @PathVariable Long cid) throws IOException {
        DoorSystem doorSys = getDoorSystem(did);
        return doorSys.readCardData(cid);
    }

    @GetMapping("/{did}/cards/{cid}")
    public Card getCard(@PathVariable Long did, @PathVariable Long cid) throws IOException {
        return cardRepo.findOne(new CardKey(did, cid));
    }

    @PostMapping("/{did}/cards")
    @Transactional
    public Card addCard(@PathVariable Long did, @RequestParam Long cid, @RequestParam Long workerId, @RequestParam(required = false, defaultValue = "false") boolean upload)
                                    throws IOException {
        Door door = repo.findOne(did);
        if (door == null) {
            throw new IllegalArgumentException("Door not found : " + did);
        }
        Worker worker = workerRepo.findOne(workerId);
        if (worker == null) {
            throw new IllegalArgumentException("Worker not found : " + workerId);
        }
        Card card = new Card(did, cid, worker);
        card = cardRepo.save(card);
        logger.info("New card added:" + card);
        if (upload) {
            DoorSystem doorSys = DoorSystemFactory.createInstance(door);
            if (null != doorSys.readCardData(cid)) {
                logger.warn("Card " + cid + " already exist in door " + door.getLabel());
            } else {
                logger.info("Adding new card " + cid + " to door " + door.getLabel());
                doorSys.addCard(new CardData[] {new CardData(cid)});
                logger.info("New card uploaded to door system :" + cid);
            }
        }
        return card;
    }

    @DeleteMapping("/{did}/cards/{cid}")
    @Transactional
    public void delCard(@PathVariable Long did, @PathVariable Long cid, @RequestParam(required = false, defaultValue = "false") boolean upload) throws IOException {
        CardKey key = new CardKey(did, cid);
        cardRepo.delete(key);
        logger.info("Card deleted:" + key);
        if (upload) {
            DoorSystem doorSys = getDoorSystem(did);
            doorSys.deleteCard(new long[] {cid});
            logger.info("Card deleted from door system :" + key);
        }
    }

    @PostMapping("/{did}/cards/{cid}/to-black-list")
    public void addCardToBlackList(@PathVariable Long did, @PathVariable Long cid) throws IOException {
        DoorSystem doorSys = getDoorSystem(did);
        doorSys.addCardToBlackList(cid);
    }

    private DoorSystem getDoorSystem(Long id) {
        Door door = repo.findOne(id);
        if (door == null) {
            throw new IllegalArgumentException("Door not found : " + id);
        }
        DoorSystem doorSys = DoorSystemFactory.createInstance(door);
        return doorSys;
    }

}
