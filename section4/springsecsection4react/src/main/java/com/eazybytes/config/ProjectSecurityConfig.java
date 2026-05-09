package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for Section 4: Database-backed authentication with R2DBC.
 *
 * KEY POINTS:
 * - CSRF is disabled here to allow POST requests from tools like Postman.
 *   In WebFlux, csrf() is configured on ServerHttpSecurity just like in MVC's HttpSecurity.
 * - The /register endpoint is permitted for user registration.
 * - No need to define a UserDetailsService bean here since EazyBankUserDetailsService
 *   is annotated with @Service and Spring auto-detects it.
 */
@Configuration
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // Disable CSRF for API testing (same reason as MVC version)
        http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register").permitAll())
                .formLogin(withDefaults())
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

}
