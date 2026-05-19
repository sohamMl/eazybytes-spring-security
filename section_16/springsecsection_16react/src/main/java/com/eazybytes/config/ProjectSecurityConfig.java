package com.eazybytes.config;

import com.eazybytes.exceptionhandling.CustomServerAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Section 15: OAuth2 Resource Server with Keycloak JWT in WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. In MVC, oauth2ResourceServer() accepts jwt(jwtConfigurer -> ...)
 *    with JwtAuthenticationConverter directly.
 *
 * 2. In WebFlux, we need to wrap JwtAuthenticationConverter with
 *    ReactiveJwtAuthenticationConverterAdapter to make it work with
 *    the reactive security pipeline.
 *
 * 3. The Keycloak JWK Set URI is configured in application.properties
 *    and Spring auto-configures the reactive JWT decoder.
 *
 * 4. Opaque token introspection is also supported in WebFlux.
 *    The commented-out section shows how to configure it.
 */
@Configuration
@Profile("!prod")
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        // Wrap MVC's JwtAuthenticationConverter for reactive use
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        ReactiveJwtAuthenticationConverterAdapter reactiveAdapter =
                new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);

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
                        .pathMatchers("/myAccount").hasRole("USER")
                        .pathMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/myLoans").authenticated()
                        .pathMatchers("/myCards").hasRole("USER")
                        .pathMatchers("/user").authenticated()
                        .pathMatchers("/notices", "/contact", "/error", "/register").permitAll())
                // Configure as OAuth2 Resource Server with JWT
                .oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(reactiveAdapter)))
                // Opaque token alternative (commented out — same as MVC version):
                // .oauth2ResourceServer(rsc -> rsc.opaqueToken(otc -> otc
                //         .authenticationConverter(new KeycloakOpaqueRoleConverter())
                //         .introspectionUri(introspectionUri)
                //         .introspectionClientCredentials(clientId, clientSecret)))
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
}
