package com.yyy.server.user.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepo extends PagingAndSortingRepository<User, Long> {
    User findOneByNameAndPassword(String name, String password);

    Page<User> findByDisplayNameLike(String displayNameLike, Pageable pageable);
}
