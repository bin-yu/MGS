package com.yyy.server.workerIncident.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.yyy.server.worker.repo.Worker;

@Repository
public interface IncidentRepo extends PagingAndSortingRepository<Incident, Long> {
    List<Incident> findBySubject(Worker subject);
}
