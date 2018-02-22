package com.yyy.server.worker.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepo extends PagingAndSortingRepository<Worker, Long> {
    List<Worker> findByInBlackList(boolean inBlackList);

    List<Worker> findByNameLike(String namePart);
}
