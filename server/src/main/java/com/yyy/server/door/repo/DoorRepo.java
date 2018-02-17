package com.yyy.server.door.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoorRepo extends PagingAndSortingRepository<Door, Long> {

}
