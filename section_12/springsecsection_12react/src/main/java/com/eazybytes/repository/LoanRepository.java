package com.eazybytes.repository;

import com.eazybytes.model.Loans;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface LoanRepository extends ReactiveCrudRepository<Loans, Long> {
    Flux<Loans> findByCustomerIdOrderByStartDtDesc(long customerId);
}
