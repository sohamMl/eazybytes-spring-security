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
 * Section 8: Full security config with CORS, CSRF, and exception handling.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. CORS: Uses org.springframework.web.cors.reactive.CorsConfigurationSource
 *    (not the servlet version).
 *
 * 2. CSRF: Uses CookieServerCsrfTokenRepository instead of CookieCsrfTokenRepository.
 *    WebFlux CSRF is configured via csrf() on ServerHttpSecurity.
 *
 * 3. Session management: WebFlux doesn't have SessionCreationPolicy.ALWAYS.
 *    WebFlux is inherently stateless with Netty. If you need sessions,
 *    use WebSession with Spring Session (e.g., backed by Redis).
 *
 * 4. securityContext(requireExplicitSave(false)) — Not applicable in WebFlux.
 *    The reactive security context is managed via ReactiveSecurityContextHolder.
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
                        // In WebFlux, we skip CSRF for specific paths using a matcher
                        .requireCsrfProtectionMatcher(exchange -> {
                            String path = exchange.getRequest().getPath().value();
                            // Skip CSRF for /contact and /register
                            if ("/contact".equals(path) || "/register".equals(path)) {
                                return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                        .MatchResult.notMatch();
                            }
                            // Require CSRF for all other paths (default behavior for state-changing methods)
                            return org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
                                    .MatchResult.match();
                        }))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards", "/user").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register", "/invalidSession").permitAll())
                .formLogin(withDefaults())
                .httpBasic(hbc -> hbc.authenticationEntryPoint(new CustomServerAuthenticationEntryPoint()))
                .exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomServerAccessDeniedHandler()));
        return http.build();
    }

    /**
     * CORS configuration source for reactive stack.
     * Uses org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
     * (not the servlet CorsConfigurationSource).
     */
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
