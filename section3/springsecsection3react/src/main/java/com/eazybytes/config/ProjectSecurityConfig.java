package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security config with in-memory user management and password encoding.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. MapReactiveUserDetailsService replaces InMemoryUserDetailsManager.
 *    - InMemoryUserDetailsManager implements UserDetailsService (blocking).
 *    - MapReactiveUserDetailsService implements ReactiveUserDetailsService (non-blocking).
 *    - Both store users in memory, but the reactive version returns Mono<UserDetails>.
 *
 * 2. PasswordEncoder is the SAME in both MVC and WebFlux — it's not tied to the web framework.
 *    - PasswordEncoderFactories.createDelegatingPasswordEncoder() creates a DelegatingPasswordEncoder
 *      that supports multiple encoding formats ({bcrypt}, {noop}, {sha256}, etc.)
 *
 * 3. CompromisedPasswordChecker (HaveIBeenPwned) works the same way.
 */
@Configuration
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        .pathMatchers("/notices", "/contact", "/error").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * MapReactiveUserDetailsService is the reactive equivalent of InMemoryUserDetailsManager.
     * It stores UserDetails objects in a Map and returns them as Mono<UserDetails>.
     *
     * The User.withUsername() builder works the same as in MVC.
     * Password prefixes like {noop} and {bcrypt} tell the DelegatingPasswordEncoder
     * which algorithm was used to encode the password.
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService() {
        // {noop} means the password is stored in plain text (not encoded)
        UserDetails user = User.withUsername("user")
                .password("{noop}EazyBytes@12345")
                .authorities("read")
                .build();
        // {bcrypt} means the password is BCrypt-hashed
        UserDetails admin = User.withUsername("admin")
                .password("{bcrypt}$2a$12$88.f6upbBvy0okEa7OfHFuorV29qeK.sVbB9VQ6J6dWM1bW6Qef8m")
                .authorities("admin")
                .build();
        return new MapReactiveUserDetailsService(user, admin);
    }

    /**
     * PasswordEncoder is framework-agnostic — the same bean works in MVC and WebFlux.
     * DelegatingPasswordEncoder delegates to the appropriate encoder based on the password prefix.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * CompromisedPasswordChecker uses the HaveIBeenPwned API to check if a password
     * has been exposed in known data breaches. Available from Spring Security 6.3+.
     * Works the same in both MVC and WebFlux.
     */
    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

}
