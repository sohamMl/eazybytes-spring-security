package com.eazybytes.config;

import com.eazybytes.repository.AuthorityRepository;
import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Updated ReactiveUserDetailsService that loads authorities from a separate table.
 *
 * KEY DIFFERENCE FROM MVC (Section 9):
 * In JPA, Customer has @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER)
 * which automatically fetches authorities when loading a customer.
 *
 * R2DBC doesn't support relationships, so we:
 * 1. First find the customer by email
 * 2. Then find all authorities for that customer's ID
 * 3. Combine them using flatMap and collectList()
 */
@Service
@RequiredArgsConstructor
public class EazyBankUserDetailsService implements ReactiveUserDetailsService {

    private final CustomerRepository customerRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return customerRepository.findByEmail(username)
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User details not found for the user: " + username)))
                .flatMap(customer ->
                        // Since R2DBC doesn't support relationships, we fetch authorities separately
                        authorityRepository.findByCustomerId(customer.getId())
                                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                                .collectList()
                                .map(authorities -> (UserDetails) new User(
                                        customer.getEmail(),
                                        customer.getPwd(),
                                        authorities))
                );
    }
}
