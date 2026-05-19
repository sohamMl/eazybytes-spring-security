package com.eazybytes.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Event listeners for authentication success and failure.
 *
 * KEY POINT: Authentication events work the SAME in both MVC and WebFlux!
 * Spring Security publishes AuthenticationSuccessEvent and AbstractAuthenticationFailureEvent
 * regardless of whether you're using servlet or reactive stack.
 * The @EventListener annotation works with Spring's application event system,
 * which is independent of the web framework.
 */
@Component
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        log.info("Login successful for the user : {}", successEvent.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        log.error("Login failed for the user : {} due to : {}", failureEvent.getAuthentication().getName(),
                failureEvent.getException().getMessage());
    }

}
