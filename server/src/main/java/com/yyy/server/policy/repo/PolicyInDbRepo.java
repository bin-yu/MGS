package com.yyy.server.policy.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyInDbRepo extends PagingAndSortingRepository<PolicyInDb, Long> {
    
}
