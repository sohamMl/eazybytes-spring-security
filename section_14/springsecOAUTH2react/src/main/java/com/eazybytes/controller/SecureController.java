package com.eazybytes.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

/**
 * Controller for the secured page.
 * In WebFlux with Thymeleaf, returning a String from a @Controller maps to a template name.
 * Thymeleaf works in reactive mode with WebFlux automatically.
 */
@Controller
public class SecureController {

    @GetMapping("/secure")
    public Mono<String> securePage(Authentication authentication) {
        if (authentication instanceof UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) {
            System.out.println(usernamePasswordAuthenticationToken);
        } else if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            System.out.println(oAuth2AuthenticationToken);
        }
        // Returns the Thymeleaf template name (templates/secure.html)
        return Mono.just("secure");
    }
}
