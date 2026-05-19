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
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Section 9: Role-based authorization with hasRole() / hasAnyRole().
 *
 * KEY CONCEPT: Role vs Authority
 * - hasAuthority("VIEWACCOUNT") — checks for exact authority name
 * - hasRole("USER") — checks for "ROLE_USER" (Spring auto-prepends "ROLE_")
 * - The pathMatchers().hasRole() API works the same in WebFlux as requestMatchers().hasRole() in MVC.
 */
@Configuration
@Profile("!prod")
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .csrf(csrfConfig -> csrfConfig
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .requireCsrfProtectionMatcher(exchange -> {
                            String path = exchange.getRequest().getPath().value();
                            if ("/contact".equals(path) || "/register".equals(path)) {
                                return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                        .MatchResult.notMatch();
                            }
                            return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                    .MatchResult.match();
                        }))
                .authorizeExchange(exchanges -> exchanges
                        // Role-based authorization (same logic as MVC version)
                        // hasRole("USER") checks for "ROLE_USER" authority
                        .pathMatchers("/myAccount").hasRole("USER")
                        .pathMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/myLoans").hasRole("USER")
                        .pathMatchers("/myCards").hasRole("USER")
                        .pathMatchers("/user").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll())
                .formLogin(withDefaults())
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomServerAccessDeniedHandler()));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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
