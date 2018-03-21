package com.yyy.server.workerIncident.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.yyy.server.domain.repo.Domain;
import com.yyy.server.worker.repo.Worker;

@Repository
public interface IncidentRepo extends PagingAndSortingRepository<Incident, Long> {
    List<Incident> findBySubject(Worker subject);

    Page<Incident> findByDomain(Domain domain, Pageable pageable);

    Page<Incident> findByDomainAndSubject_NameLike(Domain domain, String nameLike, Pageable pageable);

    Incident getByIdAndDomain(Long id, Domain domain);
}
