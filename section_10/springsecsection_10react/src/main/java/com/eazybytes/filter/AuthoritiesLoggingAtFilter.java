package com.eazybytes.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Simple logging filter that runs alongside authentication.
 * Same concept in both MVC and WebFlux — just a different interface.
 */
@Slf4j
public class AuthoritiesLoggingAtFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("Authentication Validation is in progress");
        return chain.filter(exchange);
    }
}
