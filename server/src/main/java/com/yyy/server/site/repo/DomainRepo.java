package com.yyy.server.site.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainRepo extends PagingAndSortingRepository<Domain, Long> {
    Domain findOneByName(String name);
}
