package com.eazybytes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom ReactiveAuthenticationManager for non-production environments.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, you implement AuthenticationProvider with:
 *    - Authentication authenticate(Authentication) — returns Authentication synchronously
 *    - boolean supports(Class<?>) — tells Spring which token types this provider handles
 *
 * 2. In WebFlux, you implement ReactiveAuthenticationManager with:
 *    - Mono<Authentication> authenticate(Authentication) — returns Authentication reactively
 *    - No supports() method — WebFlux uses a single ReactiveAuthenticationManager
 *
 * 3. In the MVC version, the non-prod provider skips password validation (for development ease).
 *    We replicate that same behavior here.
 *
 * NOTE: In MVC, multiple AuthenticationProviders can be registered and Spring iterates through them.
 * In WebFlux, you typically have one ReactiveAuthenticationManager. If you need multiple,
 * you can use DelegatingReactiveAuthenticationManager.
 */
@Component
@Profile("!prod")
@RequiredArgsConstructor
public class EazyBankReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        // In non-prod, we load the user but skip password validation (same as MVC version)
        return userDetailsService.findByUsername(username)
                .map(userDetails -> (Authentication) new UsernamePasswordAuthenticationToken(
                        username,
                        authentication.getCredentials(),
                        userDetails.getAuthorities()));
    }
}
