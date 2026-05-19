package com.eazybytes.repository;

import com.eazybytes.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Reactive repository for Customer entity.
 *
 * KEY DIFFERENCE FROM MVC:
 * - In MVC, CustomerRepository extends CrudRepository<Customer, Long>
 *   which returns blocking types (Optional<Customer>, List<Customer>, etc.)
 * - In WebFlux, we extend ReactiveCrudRepository<Customer, Long>
 *   which returns reactive types (Mono<Customer>, Flux<Customer>).
 * - The derived query method findByEmail() returns Mono<Customer> instead of Optional<Customer>.
 */
@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {

    /**
     * Derived query: Spring Data automatically generates the query from the method name.
     * Returns Mono<Customer> — a reactive wrapper that emits 0 or 1 Customer.
     * In the MVC version, this returned Optional<Customer>.
     */
    Mono<Customer> findByEmail(String email);

}
