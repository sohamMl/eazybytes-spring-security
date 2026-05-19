package com.eazybytes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * OAuth2 Social Login configuration for WebFlux.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. Uses ServerHttpSecurity instead of HttpSecurity.
 * 2. Uses authorizeExchange() instead of authorizeHttpRequests().
 * 3. oauth2Login() configures OAuth2 login flow in the reactive stack.
 *    The OAuth2 flow itself (redirect to provider, callback handling) works
 *    the same conceptually, but uses reactive WebClient internally.
 *
 * 4. The commented-out ClientRegistrationRepository code below shows how to
 *    programmatically configure OAuth2 clients. The same InMemoryReactiveClientRegistrationRepository
 *    is used in WebFlux (auto-configured from application.properties).
 */
@Configuration
@EnableWebFluxSecurity
public class ProjectSecurityConfig {

    @Bean
    SecurityWebFilterChain defaultSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/secure").authenticated()
                        .anyExchange().permitAll())
                .formLogin(Customizer.withDefaults())
                // oauth2Login() in WebFlux uses ReactiveOAuth2AuthorizedClientService
                .oauth2Login(Customizer.withDefaults());
        return http.build();
    }

    /*
     * Programmatic client registration (alternative to application.properties):
     *
     * @Bean
     * ReactiveClientRegistrationRepository clientRegistrationRepository() {
     *     ClientRegistration github = CommonOAuth2Provider.GITHUB.getBuilder("github")
     *             .clientId("Ov23liCBLLUjii41pS7k")
     *             .clientSecret("9da8734b56aad52d91b268fe6834a8df12447d95").build();
     *     ClientRegistration facebook = CommonOAuth2Provider.FACEBOOK.getBuilder("facebook")
     *             .clientId("974042741122392")
     *             .clientSecret("36d48c25c1767d58b3101551513d7e1e").build();
     *     return new InMemoryReactiveClientRegistrationRepository(github, facebook);
     * }
     */
}
