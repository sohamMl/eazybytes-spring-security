package com.eazybytes.controller;

import com.eazybytes.model.Customer;
import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * User registration controller - reactive version.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. Returns Mono<ResponseEntity<String>> instead of ResponseEntity<String>.
 * 2. customerRepository.save() returns Mono<Customer> (not Customer directly).
 * 3. We chain reactive operators (.map(), .onErrorResume()) instead of try/catch.
 * 4. The entire chain is non-blocking — no thread is blocked waiting for DB operations.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> registerUser(@RequestBody Customer customer) {
        // Encode the password before saving
        String hashPwd = passwordEncoder.encode(customer.getPwd());
        customer.setPwd(hashPwd);

        // customerRepository.save() returns Mono<Customer>
        // .map() transforms the saved customer into a ResponseEntity
        // .onErrorResume() handles exceptions reactively (replaces try/catch)
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

}
