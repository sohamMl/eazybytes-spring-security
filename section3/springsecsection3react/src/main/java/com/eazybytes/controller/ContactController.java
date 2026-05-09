package com.eazybytes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Contact endpoint - publicly accessible (permitAll in security config).
 */
@RestController
public class ContactController {

    @GetMapping("/contact")
    public Mono<String> saveContactInquiryDetails() {
        return Mono.just("Inquiry details are saved to the DB");
    }

}
