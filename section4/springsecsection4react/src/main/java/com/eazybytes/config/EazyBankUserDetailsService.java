package com.eazybytes.config;

import com.eazybytes.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Custom ReactiveUserDetailsService that loads user details from the database via R2DBC.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. Implements ReactiveUserDetailsService instead of UserDetailsService.
 *    - UserDetailsService.loadUserByUsername() returns UserDetails (blocking).
 *    - ReactiveUserDetailsService.findByUsername() returns Mono<UserDetails> (non-blocking).
 *
 * 2. The method is called findByUsername() (not loadUserByUsername()) in the reactive interface.
 *
 * 3. Instead of throwing UsernameNotFoundException directly, we use
 *    Mono.error() to propagate exceptions reactively, or switchIfEmpty()
 *    to handle the "user not found" case.
 *
 * 4. The repository returns Mono<Customer>, so we chain reactive operators
 *    (.map(), .switchIfEmpty()) instead of using Optional.orElseThrow().
 */
@Service
@RequiredArgsConstructor
public class EazyBankUserDetailsService implements ReactiveUserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        // customerRepository.findByEmail() returns Mono<Customer>
        // .map() transforms Customer -> UserDetails (non-blocking)
        // .switchIfEmpty() handles the case where no customer was found
        return customerRepository.findByEmail(username)
                .map(customer -> {
                    List<GrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority(customer.getRole()));
                    return (UserDetails) new User(customer.getEmail(), customer.getPwd(), authorities);
                })
                .switchIfEmpty(Mono.error(
                        new UsernameNotFoundException("User details not found for the user: " + username)));
    }
}
