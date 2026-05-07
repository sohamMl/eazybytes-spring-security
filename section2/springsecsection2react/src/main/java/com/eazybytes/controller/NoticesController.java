package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Notices endpoint - publicly accessible (permitAll in security config).
 */
@RestController
public class NoticesController {

    @GetMapping("/notices")
    public Mono<String> getNotices() {
        return Mono.just("Here are the notices details from the DB");
    }

}
