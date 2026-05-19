package com.eazybytes.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Logs authorities AFTER authentication has completed.
 *
 * KEY DIFFERENCE FROM MVC:
 * In MVC, we get Authentication from SecurityContextHolder.getContext().getAuthentication()
 * In WebFlux, we use ReactiveSecurityContextHolder.getContext() which returns Mono<SecurityContext>
 */
@Slf4j
public class AuthoritiesLoggingAfterFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .then(ReactiveSecurityContextHolder.getContext()
                        .map(context -> context.getAuthentication())
                        .doOnNext(authentication -> {
                            if (authentication != null) {
                                log.info("User {} is successfully authenticated and has the authorities {}",
                                        authentication.getName(), authentication.getAuthorities().toString());
                            }
                        })
                        .then());
    }
}
