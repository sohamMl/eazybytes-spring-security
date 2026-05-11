package com.eazybytes.config;

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
 * Security configuration for non-production profile.
 *
 * KEY CONCEPT: Profile-based security configuration works the same in both MVC and WebFlux.
 * @Profile("!prod") means this config is active for all profiles EXCEPT "prod".
 * In production, ProjectSecurityProdConfig is used instead.
 */
@Configuration
@Profile("!prod")
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
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
