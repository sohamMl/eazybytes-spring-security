package com.eazybytes.filter;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that forces the CSRF token to be loaded so it gets written as a cookie.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, CsrfCookieFilter extends OncePerRequestFilter (a servlet filter).
 * 2. In WebFlux, we implement WebFilter — the reactive equivalent of servlet Filter.
 *
 * 3. In MVC: CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
 *    In WebFlux: We get it from the exchange attributes using Mono.
 *
 * 4. WebFilter.filter() returns Mono<Void> (non-blocking) instead of void.
 */
public class CsrfWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Get the CsrfToken from exchange attributes (it's stored as a Mono)
        Mono<CsrfToken> csrfTokenMono = exchange.getAttribute(CsrfToken.class.getName());
        if (csrfTokenMono != null) {
            // Subscribe to force the token to be generated and written as a cookie
            return csrfTokenMono
                    .doOnSuccess(csrfToken -> {
                        // Calling getToken() forces the deferred token to be loaded
                        csrfToken.getToken();
                    })
                    .then(chain.filter(exchange));
        }
        return chain.filter(exchange);
    }
}
