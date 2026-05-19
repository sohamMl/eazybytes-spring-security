package com.eazybytes.repository;

import com.eazybytes.model.AccountTransactions;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AccountTransactionsRepository extends ReactiveCrudRepository<AccountTransactions, String> {
    Flux<AccountTransactions> findByCustomerIdOrderByTransactionDtDesc(long customerId);
}
