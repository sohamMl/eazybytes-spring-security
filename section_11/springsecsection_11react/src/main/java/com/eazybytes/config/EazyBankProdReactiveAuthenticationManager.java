package com.eazybytes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Custom ReactiveAuthenticationManager for PRODUCTION environments.
 *
 * This version DOES validate the password (unlike the non-prod version).
 * In the MVC version, EazyBankProdUsernamePwdAuthenticationProvider uses
 * passwordEncoder.matches() to verify the password.
 * We do the same here, but reactively.
 */
@Component
@Profile("prod")
@RequiredArgsConstructor
public class EazyBankProdReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String pwd = authentication.getCredentials().toString();

        return userDetailsService.findByUsername(username)
                .flatMap(userDetails -> {
                    // Validate password using PasswordEncoder
                    if (passwordEncoder.matches(pwd, userDetails.getPassword())) {
                        return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(
                                username, pwd, userDetails.getAuthorities()));
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid password!"));
                    }
                });
    }
}
