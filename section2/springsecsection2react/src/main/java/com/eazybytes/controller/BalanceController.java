package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class BalanceController {

    @GetMapping("/myBalance")
    public Mono<String> getBalanceDetails() {
        return Mono.just("Here are the balance details from the DB");
    }

}
