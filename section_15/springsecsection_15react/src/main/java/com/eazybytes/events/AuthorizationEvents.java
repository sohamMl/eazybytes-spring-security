package com.eazybytes.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

/**
 * Event listener for authorization denied events.
 * Works the same in both MVC and WebFlux — Spring events are framework-agnostic.
 */
@Component
@Slf4j
public class AuthorizationEvents {

    @EventListener
    public void onFailure(AuthorizationDeniedEvent deniedEvent) {
        log.error("Authorization failed for the user : {} due to : {}",
                deniedEvent.getAuthentication().get().getName(),
                deniedEvent.getAuthorizationResult());
    }
}
