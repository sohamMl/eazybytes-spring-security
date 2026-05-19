package com.eazybytes.controller;

import com.eazybytes.model.Accounts;
import com.eazybytes.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Account controller - returns Mono<Accounts> from R2DBC repository.
 */
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsRepository accountsRepository;

    @GetMapping("/myAccount")
    public Mono<Accounts> getAccountDetails(@RequestParam long id) {
        // ReactiveCrudRepository.findByCustomerId() returns Mono<Accounts>
        return accountsRepository.findByCustomerId(id);
    }
}
