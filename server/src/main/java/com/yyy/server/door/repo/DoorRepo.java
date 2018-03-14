package com.yyy.server.door.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.yyy.server.domain.repo.Domain;

@Repository
public interface DoorRepo extends PagingAndSortingRepository<Door, Long> {
	Page<Door> findByDomain(Domain domain,Pageable pageable);
	Door getByIdAndDomain(Long id,Domain domain);
	
}
