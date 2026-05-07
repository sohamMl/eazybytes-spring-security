package com.eazybytes.springsecsection1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * A simple REST controller to demonstrate Spring Security with WebFlux.
 *
 * KEY DIFFERENCE FROM MVC:
 * - In MVC, methods return plain objects (e.g., String).
 * - In WebFlux, methods return reactive types: Mono<T> (0 or 1 item) or Flux<T> (0 to N items).
 * - Mono.just() wraps a value into a reactive stream that emits exactly one item.
 * - The @RestController and @GetMapping annotations work the same in both MVC and WebFlux.
 *
 * WHY REACTIVE?
 * - Reactive programming uses non-blocking I/O, which means the server thread is not held
 *   while waiting for a response. This allows handling many more concurrent requests
 *   with fewer threads compared to the traditional thread-per-request servlet model.
 */
@RestController
public class WelcomeController {

    @GetMapping("/welcome")
    public Mono<String> sayWelcome() {
        // Mono.just() creates a Mono that immediately emits the given value.
        // In a real application, this could be a database call or external API call
        // that returns a Mono, making the entire chain non-blocking.
        return Mono.just("Welcome to Spring Application with security (Reactive/WebFlux)");
    }

}
