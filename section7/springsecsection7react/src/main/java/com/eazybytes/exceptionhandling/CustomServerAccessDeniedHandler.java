package com.eazybytes.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Custom handler for access denied (403 Forbidden) errors in WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, you implement AccessDeniedHandler with:
 *    - void handle(HttpServletRequest, HttpServletResponse, AccessDeniedException)
 *    - You write directly to HttpServletResponse (blocking I/O).
 *
 * 2. In WebFlux, you implement ServerAccessDeniedHandler with:
 *    - Mono<Void> handle(ServerWebExchange, AccessDeniedException)
 *    - ServerWebExchange replaces HttpServletRequest/HttpServletResponse.
 *    - You write to the response body using reactive DataBuffer (non-blocking).
 *
 * 3. ServerWebExchange provides access to both request and response via:
 *    - exchange.getRequest() — ServerHttpRequest (not HttpServletRequest)
 *    - exchange.getResponse() — ServerHttpResponse (not HttpServletResponse)
 */
public class CustomServerAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        LocalDateTime currentTimeStamp = LocalDateTime.now();
        String message = (denied != null && denied.getMessage() != null)
                ? denied.getMessage() : "Authorization failed";
        String path = exchange.getRequest().getPath().value();

        // Set response headers and status
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().add("eazybank-denied-reason", "Authorization failed");
        exchange.getResponse().getHeaders().add("Content-Type", "application/json;charset=UTF-8");

        // Build JSON response body
        String jsonResponse = String.format(
                "{\"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\", \"path\": \"%s\"}",
                currentTimeStamp, HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(),
                message, path);

        // Write the response body reactively using DataBufferFactory
        byte[] bytes = jsonResponse.getBytes();
        return exchange.getResponse().writeWith(
                Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }
}
