package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Custom security configuration for WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. @EnableWebFluxSecurity replaces @EnableWebSecurity (though Spring Boot auto-configures this).
 * 2. ServerHttpSecurity replaces HttpSecurity.
 * 3. SecurityWebFilterChain replaces SecurityFilterChain.
 * 4. pathMatchers() replaces requestMatchers() for URL pattern matching.
 * 5. The builder uses .and() chaining less; instead each section is configured via lambdas.
 *
 * AUTHORIZATION RULES (same logic as MVC version):
 * - /myAccount, /myBalance, /myLoans, /myCards → require authentication
 * - /notices, /contact, /error → permit all (public access)
 */
@Configuration
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // In WebFlux, we use pathMatchers() instead of requestMatchers()
        // The authorization rules work the same conceptually, but the API is slightly different.
        http.authorizeExchange(exchanges -> exchanges
                        // These paths require the user to be authenticated
                        .pathMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                        // These paths are publicly accessible
                        .pathMatchers("/notices", "/contact", "/error").permitAll())
                // formLogin() in WebFlux redirects to a built-in login page (similar to MVC).
                // It uses ServerAuthenticationSuccessHandler and ServerAuthenticationFailureHandler.
                .formLogin(withDefaults())
                // httpBasic() enables HTTP Basic authentication for programmatic clients (e.g., Postman, curl).
                .httpBasic(withDefaults());
        return http.build();
    }

}
