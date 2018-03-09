package com.yyy.server.domain.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainRepo extends PagingAndSortingRepository<Domain, Long> {
    Domain getByName(String name);
}
