package com.eazybytes.controller;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * User controller for registration and fetching logged-in user details.
 *
 * KEY DIFFERENCE: In WebFlux, the Authentication object is obtained from
 * ReactiveSecurityContextHolder, but Spring automatically injects it
 * as a method parameter just like in MVC.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody Customer customer) {
        String hashPwd = passwordEncoder.encode(customer.getPwd());
        customer.setPwd(hashPwd);
        customer.setCreateDt(java.time.LocalDate.now().toString());

        return customerRepository.save(customer)
                .map(savedCustomer -> {
                    if (savedCustomer.getId() > 0) {
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body("Given user details are successfully registered");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("User registration failed");
                    }
                })
                .onErrorResume(ex -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("An exception occurred: " + ex.getMessage())));
    }

    @RequestMapping("/user")
    public Mono<Customer> getUserDetailsAfterLogin(Authentication authentication) {
        return customerRepository.findByEmail(authentication.getName());
    }
}
