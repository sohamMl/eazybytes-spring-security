package com.eazybytes.repository;

import com.eazybytes.model.Contact;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends ReactiveCrudRepository<Contact, String> {
}
