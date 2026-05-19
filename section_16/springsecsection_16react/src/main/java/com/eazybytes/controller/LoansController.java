package com.eazybytes.controller;

import com.eazybytes.model.Loans;
import com.eazybytes.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Loans controller with method-level security using @PostAuthorize.
 *
 * KEY DIFFERENCES FROM MVC:
 * 1. @PreAuthorize and @PostAuthorize work with reactive return types (Mono/Flux)
 *    when @EnableReactiveMethodSecurity is enabled.
 *
 * 2. The SpEL expressions (e.g., hasRole('USER')) are the same in both MVC and WebFlux.
 *
 * 3. @PostAuthorize is evaluated AFTER the method executes but BEFORE the result
 *    is returned to the client. If the check fails, the result is discarded
 *    and an AccessDeniedException is thrown.
 */
@RestController
@RequiredArgsConstructor
public class LoansController {

    private final LoanRepository loanRepository;

    @GetMapping("/myLoans")
    @PostAuthorize("hasRole('USER')")
    public Flux<Loans> getLoanDetails(@RequestParam long id) {
        return loanRepository.findByCustomerIdOrderByStartDtDesc(id);
    }
}
