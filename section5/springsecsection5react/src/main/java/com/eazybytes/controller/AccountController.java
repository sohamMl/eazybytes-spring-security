package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Returns account details. This endpoint is SECURED (requires authentication).
 * In WebFlux, we return Mono<String> instead of plain String.
 */
@RestController
public class AccountController {

    @GetMapping("/myAccount")
    public Mono<String> getAccountDetails() {
        return Mono.just("Here are the account details from the DB");
    }

}
