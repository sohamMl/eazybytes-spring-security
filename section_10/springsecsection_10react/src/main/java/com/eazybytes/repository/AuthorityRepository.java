package com.eazybytes.repository;

import com.eazybytes.model.Authority;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AuthorityRepository extends ReactiveCrudRepository<Authority, Long> {
    Flux<Authority> findByCustomerId(Long customerId);
}
