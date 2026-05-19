package com.eazybytes.controller;

import com.eazybytes.constants.ApplicationConstants;
import com.eazybytes.model.Customer;
import com.eazybytes.model.LoginRequestDTO;
import com.eazybytes.model.LoginResponseDTO;
import com.eazybytes.repository.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * User controller with JWT-based /apiLogin endpoint.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. Uses ReactiveAuthenticationManager instead of AuthenticationManager.
 * 2. authenticationManager.authenticate() returns Mono<Authentication>.
 * 3. All return types are Mono<> for non-blocking execution.
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReactiveAuthenticationManager authenticationManager;
    private final Environment env;

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

    @PostMapping("/apiLogin")
    public Mono<ResponseEntity<LoginResponseDTO>> apiLogin(@RequestBody LoginRequestDTO loginRequest) {
        // Create an unauthenticated token
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.username(), loginRequest.password());

        // ReactiveAuthenticationManager.authenticate() returns Mono<Authentication>
        return authenticationManager.authenticate(authentication)
                .map(authenticationResponse -> {
                    String jwt = "";
                    if (authenticationResponse.isAuthenticated()) {
                        String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                        jwt = Jwts.builder().issuer("Eazy Bank").subject("JWT Token")
                                .claim("username", authenticationResponse.getName())
                                .claim("authorities", authenticationResponse.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .collect(Collectors.joining(",")))
                                .issuedAt(new java.util.Date())
                                .expiration(new java.util.Date(new java.util.Date().getTime() + 30000000))
                                .signWith(secretKey).compact();
                    }
                    return ResponseEntity.status(HttpStatus.OK)
                            .header(ApplicationConstants.JWT_HEADER, jwt)
                            .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
                });
    }
}
