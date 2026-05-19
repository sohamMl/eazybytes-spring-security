package com.eazybytes.filter;

import com.eazybytes.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * WebFilter that generates a JWT token after successful authentication.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, extends OncePerRequestFilter and overrides doFilterInternal().
 * 2. In WebFlux, implements WebFilter and overrides filter().
 * 3. In MVC, we get Authentication from SecurityContextHolder (ThreadLocal).
 * 4. In WebFlux, we get it from ReactiveSecurityContextHolder (Reactor Context).
 * 5. shouldNotFilter() logic is handled inline since WebFilter doesn't have that method.
 */
@Slf4j
public class JWTTokenGeneratorFilter implements WebFilter {

    private final Environment env;

    public JWTTokenGeneratorFilter(Environment env) {
        this.env = env;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.defer(() -> {
                    // Only generate JWT for the /user endpoint
                    String path = exchange.getRequest().getPath().value();
                    if (!"/user".equals(path)) {
                        return Mono.empty();
                    }

                    return ReactiveSecurityContextHolder.getContext()
                            .map(context -> context.getAuthentication())
                            .doOnNext(authentication -> {
                                if (authentication != null) {
                                    String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                                            ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                                    String jwt = Jwts.builder()
                                            .issuer("Eazy Bank")
                                            .subject("JWT Token")
                                            .claim("username", authentication.getName())
                                            .claim("authorities", authentication.getAuthorities().stream()
                                                    .map(GrantedAuthority::getAuthority)
                                                    .collect(Collectors.joining(",")))
                                            .issuedAt(new Date())
                                            .expiration(new Date(new Date().getTime() + 30000000))
                                            .signWith(secretKey).compact();
                                    exchange.getResponse().getHeaders().add(ApplicationConstants.JWT_HEADER, jwt);
                                }
                            })
                            .then();
                }));
    }
}
