package com.yyy.server.worker.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

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
import com.yyy.server.domain.repo.Domain;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.worker.repo.WorkerRepo;
import com.yyy.server.workerIncident.repo.Incident;
import com.yyy.server.workerIncident.repo.IncidentRepo;

@RestController
@RequestMapping({"/domains/{domainId}/workers"})
public class WorkerController {
	private Logger logger = LoggerFactory.getLogger(WorkerController.class);
    @Autowired
    private WorkerRepo repo;
    @Autowired
    private IncidentRepo incRepo;
    @Autowired
    private CardRepo cardRepo;

    @GetMapping
    public Page<Worker> getWorkers(@PathVariable Long domainId,Pageable pageable) throws Exception {
    	return repo.findByDomain(new Domain(domainId), pageable);
    }

    @GetMapping("/search")
    public Page<Worker> findWorkerByNameLike(@PathVariable Long domainId,@RequestParam() String nameLike, Pageable pageable) throws Exception {
        return repo.findByDomainAndNameLike(new Domain(domainId),nameLike, pageable);
    }

    @GetMapping("/{id}")
    public Worker getWorker(@PathVariable Long domainId,@PathVariable Long id) {
        Worker worker = repo.getByIdAndDomain(id, new Domain(domainId));
        if(worker==null){
        	throw new EntityNotFoundException("Worker not found for id : "+id);
        }
		return worker;
    }

    @PostMapping()
    public Worker addWorker(@PathVariable Long domainId,@RequestBody Worker worker) {
    	worker.setDomain(new Domain(domainId));
        return repo.save(worker);
    }

    @PutMapping("/{id}")
    public Worker updateWorker(@PathVariable Long domainId,@PathVariable Long id, @RequestBody Worker worker) {
        if (!id.equals(worker.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        worker.setDomain(new Domain(domainId));
        return repo.save(worker);
    }

    @DeleteMapping("/{id}")
    public void deleteWorker(@PathVariable Long domainId,@PathVariable Long id) {
        repo.delete(getWorker(domainId, id));
    }

    @GetMapping("/{id}/incidents")
    public List<Incident> getIncidents(@PathVariable Long domainId,@PathVariable Long id) {
        Worker worker = this.getWorker(domainId, id);
        return incRepo.findBySubject(worker);
    }

    @GetMapping("/{id}/cards")
    public List<Card> getCards(@PathVariable Long domainId,@PathVariable Long id) {
        Worker worker = this.getWorker(domainId, id);
        return cardRepo.findByWorker(worker);
    }
    @RequestMapping({"/workers"})
    @GetMapping("/blackList")
    public List<Worker> getBlackListWorkers() throws Exception {
        return repo.findByInBlackList(true);
    }
}
