package com.eazybytes.repository;

import com.eazybytes.model.Cards;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CardsRepository extends ReactiveCrudRepository<Cards, Long> {
    Flux<Cards> findByCustomerId(long customerId);
}
