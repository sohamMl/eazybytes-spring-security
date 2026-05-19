package com.eazybytes.repository;

import com.eazybytes.model.Accounts;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AccountsRepository extends ReactiveCrudRepository<Accounts, Long> {
    Mono<Accounts> findByCustomerId(long customerId);
}
