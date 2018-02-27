package com.yyy.server.worker.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepo extends PagingAndSortingRepository<Worker, Long> {
    List<Worker> findByInBlackList(boolean inBlackList);

    Page<Worker> findByNameLike(String namePart, Pageable pageable);
}
