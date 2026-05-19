package com.eazybytes.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Custom entry point for authentication failures (401 Unauthorized) in WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, you implement AuthenticationEntryPoint with:
 *    - void commence(HttpServletRequest, HttpServletResponse, AuthenticationException)
 *
 * 2. In WebFlux, you implement ServerAuthenticationEntryPoint with:
 *    - Mono<Void> commence(ServerWebExchange, AuthenticationException)
 *    - Returns Mono<Void> instead of void (non-blocking).
 *
 * This is called when an unauthenticated user tries to access a secured endpoint
 * via HTTP Basic authentication.
 */
public class CustomServerAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException authException) {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (authException != null && authException.getMessage() != null)
                ? authException.getMessage() : "Unauthorized";
        String path = exchange.getRequest().getPath().value();

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("eazybank-error-reason", "Authentication failed");
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                currentTimeStamp, HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                message, path);

        byte[] bytes = jsonResponse.getBytes();
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
