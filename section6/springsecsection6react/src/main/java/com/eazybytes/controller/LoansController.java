package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LoansController {

    @GetMapping("/myLoans")
    public Mono<String> getLoanDetails() {
        return Mono.just("Here are the loan details from the DB");
    }

}
