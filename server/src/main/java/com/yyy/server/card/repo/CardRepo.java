package com.yyy.server.card.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.yyy.server.card.repo.Card.CardKey;
import com.yyy.server.worker.repo.Worker;

@Repository
public interface CardRepo extends PagingAndSortingRepository<Card, CardKey> {
    Page<Card> findByDoorId(long doorId, Pageable pagable);

    Page<Card> findByDoorIdAndWorker_NameLike(long doorId, String nameLike, Pageable pageable);
    List<Card> findByWorker(Worker worker);
}
