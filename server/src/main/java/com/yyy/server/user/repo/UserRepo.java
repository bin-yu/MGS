package com.yyy.server.user.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.yyy.server.domain.repo.Domain;

public interface UserRepo extends PagingAndSortingRepository<User, Long> {
	User findOneByNameAndPassword(String name, String password);
	Page<User> findByDomain(Domain domain,Pageable pageable);
	User getByIdAndDomain(Long id,Domain domain);
	Page<User> findByDomainAndDisplayNameLike(Domain domain,String displayNameLike, Pageable pageable);

}
