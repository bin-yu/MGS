package com.yyy.server.workerIncident.service;

import java.util.Date;

import javax.persistence.EntityNotFoundException;

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

import com.yyy.server.domain.repo.Domain;
import com.yyy.server.policy.executor.PolicyExecutor;
import com.yyy.server.worker.repo.Worker;
import com.yyy.server.worker.repo.WorkerRepo;
import com.yyy.server.workerIncident.repo.Incident;
import com.yyy.server.workerIncident.repo.IncidentRepo;

@RestController
@RequestMapping({"/domains/{domainId}/incidents"})
public class IncidentController {
    @Autowired
    private IncidentRepo repo;
    @Autowired
    private WorkerRepo workerRepo;
    @Autowired
    private PolicyExecutor policyExec;

    @GetMapping
    public Page<Incident> getIncidents(@PathVariable Long domainId, Pageable pageable) throws Exception {
        return repo.findByDomain(new Domain(domainId), pageable);
    }

    @GetMapping("/search")
    public Page<Incident> findByWorkerNameLike(@PathVariable Long domainId, @RequestParam() String workerNameLike, Pageable pageable) throws Exception {
        return repo.findByDomainAndSubject_NameLike(new Domain(domainId), workerNameLike, pageable);
    }
    @GetMapping("/{id}")
    public Incident getIncident(@PathVariable Long domainId, @PathVariable Long id) {
        Incident inc = repo.getByIdAndDomain(id, new Domain(domainId));
        if (inc == null) {
            throw new EntityNotFoundException("Incident not found for id : " + id);
        }
        return inc;
    }

    @PostMapping()
    @Transactional(rollbackFor = Throwable.class)
    public Incident addIncident(@PathVariable Long domainId, @RequestBody Incident incident) {
        incident.setRecordTime(new Date());
        incident.setDomain(new Domain(domainId));
        return createOrUpdateIncident(null, incident);
    }

    @PutMapping("/{id}")
    @Transactional(rollbackFor = Throwable.class)
    public Incident updateIncident(@PathVariable Long domainId, @PathVariable Long id, @RequestBody Incident incident) {
        if (!id.equals(incident.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        incident.setRecordTime(new Date());
        incident.setDomain(new Domain(domainId));
        return createOrUpdateIncident(getIncident(domainId, id), incident);
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Throwable.class)
    public void deleteIncident(@PathVariable Long domainId, @PathVariable Long id) throws Exception {
        Incident incident = getIncident(domainId, id);
        adjustScoreFor(incident, false);
        repo.delete(incident);
    }

    private Incident createOrUpdateIncident(Incident incidentDb, Incident incident) {
        if (incident.getSubject() == null) {
            throw new IllegalArgumentException("Incident must have one subject!");
        }
        if (incidentDb != null) {
            adjustScoreFor(incidentDb, false);
        }
        incident = repo.save(incident);

        adjustScoreFor(incident, true);
        policyExec.runOnWorker(incident.getSubject());
        return incident;
    }

    private void adjustScoreFor(Incident incident, boolean isAdd) {
        Worker w = workerRepo.findOne(incident.getSubject().getId());
        if (isAdd) {
            w.setScore(w.getScore() + incident.calScore());
        } else {
            w.setScore(w.getScore() - incident.calScore());
        }
        workerRepo.save(w);
    }
}
