package com.eazybytes.config;

import com.eazybytes.exceptionhandling.CustomServerAccessDeniedHandler;
import com.eazybytes.exceptionhandling.CustomServerAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security config with custom exception handling for WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. Session management (invalidSessionUrl, maximumSessions) does NOT have direct equivalents
 *    in WebFlux because WebFlux is inherently stateless (no servlet sessions).
 *    - If you need session-like behavior in WebFlux, you'd use WebSession,
 *      but it works differently from servlet HttpSession.
 *    - For API-based apps, stateless JWT tokens are the recommended approach (covered in Section 11).
 *
 * 2. Exception handling uses reactive types:
 *    - exceptionHandling(ehc -> ehc.accessDeniedHandler(...)) takes a ServerAccessDeniedHandler
 *    - httpBasic(hbc -> hbc.authenticationEntryPoint(...)) takes a ServerAuthenticationEntryPoint
 */
@Configuration
@Profile("!prod")
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // NOTE: WebFlux doesn't have session management like MVC's:
        //   .sessionManagement(smc -> smc.invalidSessionUrl("/invalidSession").maximumSessions(3))
        // WebFlux sessions work via WebSession (backed by in-memory or Redis stores).
        // For stateless APIs, sessions aren't needed at all.

        http.csrf(csrfConfig -> csrfConfig.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll())
                .formLogin(withDefaults())
                // Custom entry point for HTTP Basic auth failures (401)
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
                // Custom handler for access denied errors (403)
                .exceptionHandling(ehc -> ehc
                        .accessDeniedHandler(new CustomServerAccessDeniedHandler()));
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
