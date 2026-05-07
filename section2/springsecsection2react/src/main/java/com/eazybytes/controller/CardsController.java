package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class CardsController {

    @GetMapping("/myCards")
    public Mono<String> getCardDetails() {
        return Mono.just("Here are the card details from the DB");
    }

}
