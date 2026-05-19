package com.eazybytes.config;

import com.eazybytes.exceptionhandling.CustomServerAccessDeniedHandler;
import com.eazybytes.exceptionhandling.CustomServerAuthenticationEntryPoint;
import com.eazybytes.filter.JWTTokenGeneratorFilter;
import com.eazybytes.filter.JWTTokenValidatorFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Section 12: Method-Level Security with @EnableReactiveMethodSecurity.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. @EnableMethodSecurity → @EnableReactiveMethodSecurity
 *    This enables @PreAuthorize, @PostAuthorize on reactive methods.
 *
 * 2. Methods annotated with @PreAuthorize or @PostAuthorize must return
 *    reactive types (Mono/Flux) for the reactive method security to work correctly.
 *
 * 3. The /myLoans endpoint uses .authenticated() instead of .hasRole("USER")
 *    because the @PostAuthorize annotation on the controller method handles the role check.
 */
@Configuration
@Profile("!prod")
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http, Environment env) {
        http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .csrf(csrfConfig -> csrfConfig
                        .csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
                        .requireCsrfProtectionMatcher(exchange -> {
                            String path = exchange.getRequest().getPath().value();
                            if ("/contact".equals(path) || "/register".equals(path) || "/apiLogin".equals(path)) {
                                return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                        .MatchResult.notMatch();
                            }
                            return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                    .MatchResult.match();
                        }))
                .addFilterAfter(new JWTTokenGeneratorFilter(env), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterBefore(new JWTTokenValidatorFilter(env), SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/myAccount").hasRole("USER")
                        .pathMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        // Method-level security handles authorization for /myLoans via @PostAuthorize
                        .pathMatchers("/myLoans").authenticated()
                        .pathMatchers("/myCards").hasRole("USER")
                        .pathMatchers("/user").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register", "/invalidSession", "/apiLogin").permitAll())
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
        config.setExposedHeaders(Arrays.asList("Authorization"));
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

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder);
        return authManager;
    }
}
