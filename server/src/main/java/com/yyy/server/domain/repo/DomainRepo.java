package com.yyy.server.domain.repo;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DomainRepo extends PagingAndSortingRepository<Domain, Long> {
    Domain getByName(String name);
    @Query(value = "WITH RECURSIVE DTEMP(ID,PARENT_ID) as("
    		+ "Select ID,PARENT_ID from DOMAIN WHERE ID = ? "
    		+ "UNION "
    		+ "select p.ID,p.PARENT_ID from DTEMP c, DOMAIN p where c.PARENT_ID = p.ID "
    		+ ") select ID from DTEMP", nativeQuery=true)
    List<BigInteger> findAllParentIds(Long id);
}
