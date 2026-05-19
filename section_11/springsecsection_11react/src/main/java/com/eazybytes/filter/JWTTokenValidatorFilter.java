package com.eazybytes.filter;

import com.eazybytes.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * WebFilter that validates JWT tokens on incoming requests.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, sets SecurityContextHolder.getContext().setAuthentication()
 * 2. In WebFlux, we add the Authentication to the reactive context using
 *    ReactiveSecurityContextHolder (via Reactor's subscriberContext).
 * 3. shouldNotFilter() is handled inline — skip validation for /user endpoint.
 */
public class JWTTokenValidatorFilter implements WebFilter {

    private final Environment env;

    public JWTTokenValidatorFilter(Environment env) {
        this.env = env;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        // Skip JWT validation for the /user endpoint (that's where JWT is generated)
        if ("/user".equals(path)) {
            return chain.filter(exchange);
        }

        String jwt = exchange.getRequest().getHeaders().getFirst(ApplicationConstants.JWT_HEADER);
        if (jwt != null) {
            try {
                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser().verifyWith(secretKey)
                        .build().parseSignedClaims(jwt).getPayload();
                String username = String.valueOf(claims.get("username"));
                String authorities = String.valueOf(claims.get("authorities"));
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        username, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

                // In WebFlux, we propagate the authentication through Reactor's Context
                return chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder
                                .withSecurityContext(Mono.just(new SecurityContextImpl(authentication))));
            } catch (Exception exception) {
                return Mono.error(new BadCredentialsException("Invalid Token received!"));
            }
        }
        return chain.filter(exchange);
    }
}
