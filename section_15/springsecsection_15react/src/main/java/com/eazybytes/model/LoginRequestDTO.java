package com.eazybytes.model;

/**
 * DTO for login requests — same in both MVC and WebFlux.
 * Uses Java records for concise, immutable data classes.
 */
public record LoginRequestDTO(String username, String password) {
}
