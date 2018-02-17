package com.yyy.server.user.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepo extends PagingAndSortingRepository<User, Long> {
    User findOneByNameAndPassword(String name, String password);
}
