package com.yyy.server.worker.repo;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.yyy.server.domain.repo.Domain;

@Repository
public interface WorkerRepo extends PagingAndSortingRepository<Worker, Long> {
    List<Worker> findByInBlackList(Boolean inBlackList);

    Page<Worker> findByDomain(Domain domain,Pageable pageable);
    Worker getByIdAndDomain(Long id,Domain domain);
	Page<Worker> findByDomainAndNameLike(Domain domain,String nameLike, Pageable pageable);
	Page<Worker> findByDomainInAndNameLike(Collection<Domain> domains,String nameLike, Pageable pageable);

}
