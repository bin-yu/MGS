package com.yyy.server.worker.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.yyy.server.card.repo.CardRepo;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.worker.repo.WorkerRepo;
import com.yyy.server.workerIncident.repo.Incident;
import com.yyy.server.workerIncident.repo.IncidentRepo;

@RestController
@RequestMapping({"/workers"})
public class WorkerController {
    private Logger logger = LoggerFactory.getLogger(WorkerController.class);
    @Autowired
    private WorkerRepo repo;
    @Autowired
    private IncidentRepo incRepo;
    @Autowired
    private CardRepo cardRepo;

    @GetMapping
    public Page<Worker> getWorkers(Pageable pageable) throws Exception {
        Page<Worker> workers = repo.findAll(pageable);
        logger.info("getWorkers: returned worker count:" + workers.getNumberOfElements());
        return workers;
    }

    @GetMapping("/search")
    public List<Worker> findWorkerByNameLike(@RequestParam() String nameLike) throws Exception {
        return repo.findByNameLike(nameLike);
    }
    @GetMapping("/{id}")
    public Worker getWorker(@PathVariable Long id) {
        return repo.findOne(id);
    }

    @PostMapping()
    public Worker addWorker(@RequestBody Worker worker) {
        return repo.save(worker);
    }

    @PutMapping("/{id}")
    public Worker updateWorker(@PathVariable Long id, @RequestBody Worker worker) {
        if (!id.equals(worker.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        return repo.save(worker);
    }

    @DeleteMapping("/{id}")
    public void deleteWorker(@PathVariable Long id) {
        repo.delete(id);
    }

    @GetMapping("/{id}/incidents")
    public List<Incident> getIncidents(@PathVariable Long id) {
        Worker worker=repo.findOne(id);
        if(worker == null){
            throw new IllegalArgumentException("worker not found : " + id);
        }
        return incRepo.findBySubject(worker);
    }

    @GetMapping("/{id}/cards")
    public List<Card> getCards(@PathVariable Long id) {
        Worker worker = repo.findOne(id);
        if (worker == null) {
            throw new IllegalArgumentException("worker not found : " + id);
        }
        return cardRepo.findByWorker(worker);
    }

    @GetMapping("/blackList")
    public List<Worker> getBlackListWorkers() throws Exception {
        return repo.findByInBlackList(true);
    }
}
