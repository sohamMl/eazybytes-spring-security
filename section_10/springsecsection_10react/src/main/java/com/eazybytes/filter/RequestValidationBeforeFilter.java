package com.eazybytes.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Custom WebFilter that validates the request BEFORE authentication.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, this implements jakarta.servlet.Filter.
 * 2. In WebFlux, this implements org.springframework.web.server.WebFilter.
 * 3. The filter() method returns Mono<Void> instead of calling filterChain.doFilter().
 * 4. We use ServerWebExchange instead of HttpServletRequest/HttpServletResponse.
 *
 * This filter checks if the email in Basic Auth contains "test" and rejects it.
 */
@Slf4j
public class RequestValidationBeforeFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null) {
            header = header.trim();
            if (header.toLowerCase().startsWith("basic ")) {
                byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
                try {
                    byte[] decoded = Base64.getDecoder().decode(base64Token);
                    String token = new String(decoded, StandardCharsets.UTF_8);
                    int delim = token.indexOf(":");
                    if (delim == -1) {
                        return Mono.error(new BadCredentialsException("Invalid basic authentication token"));
                    }
                    String email = token.substring(0, delim);
                    if (email.toLowerCase().contains("test")) {
                        exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                        return exchange.getResponse().setComplete();
                    }
                } catch (IllegalArgumentException exception) {
                    return Mono.error(new BadCredentialsException("Failed to decode basic authentication token"));
                }
            }
        }
        return chain.filter(exchange);
    }
}
