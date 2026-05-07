package com.eazybytes.springsecsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the reactive Spring Security application.
 *
 * KEY DIFFERENCE FROM MVC:
 * - In MVC, Spring Boot auto-configures a Tomcat-based servlet container.
 * - In WebFlux, Spring Boot auto-configures Netty as the reactive server.
 * - The @SpringBootApplication annotation works the same way in both cases.
 * - Spring Boot detects WebFlux on the classpath and automatically configures
 *   reactive security (ServerHttpSecurity) instead of servlet security (HttpSecurity).
 */
@SpringBootApplication
public class EazyBankBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(EazyBankBackendApplication.class, args);
	}

}
