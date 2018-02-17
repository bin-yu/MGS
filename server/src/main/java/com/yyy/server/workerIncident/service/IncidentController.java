package com.yyy.server.workerIncident.service;

import java.util.Date;

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
import org.springframework.web.bind.annotation.RestController;

import com.yyy.server.workerIncident.repo.Incident;
import com.yyy.server.workerIncident.repo.IncidentRepo;

@RestController
@RequestMapping({"/incidents"})
public class IncidentController {
    @Autowired
    private IncidentRepo repo;

    @GetMapping
    public Page<Incident> getIncidents(Pageable pageable) throws Exception {
        Page<Incident> incidents = repo.findAll(pageable);
        return incidents;
    }

    @GetMapping("/{id}")
    public Incident getIncident(@PathVariable Long id) {
        return repo.findOne(id);
    }

    @PostMapping()
    public Incident addIncident(@RequestBody Incident incident) {

        incident.setRecordTime(new Date());
        return repo.save(incident);
    }

    @PutMapping("/{id}")
    public Incident updateIncident(@PathVariable Long id, @RequestBody Incident incident) {
        if (!id.equals(incident.getId())) {
            throw new IllegalArgumentException("Mismatched id between path variable and request body.");
        }
        incident.setRecordTime(new Date());
        return repo.save(incident);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void deleteIncident(@PathVariable Long id) {
        repo.delete(id);
    }

}
