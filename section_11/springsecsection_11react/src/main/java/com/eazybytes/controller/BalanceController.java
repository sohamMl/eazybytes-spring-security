package com.eazybytes.controller;

import com.eazybytes.model.AccountTransactions;
import com.eazybytes.repository.AccountTransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final AccountTransactionsRepository accountTransactionsRepository;

    @GetMapping("/myBalance")
    public Flux<AccountTransactions> getBalanceDetails(@RequestParam long id) {
        // Flux<T> emits 0..N items (a list of transactions)
        return accountTransactionsRepository.findByCustomerIdOrderByTransactionDtDesc(id);
    }
}
