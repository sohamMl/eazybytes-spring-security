package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Customer entity for R2DBC.
 *
 * KEY DIFFERENCES FROM JPA:
 * 1. Uses @Table from org.springframework.data.relational.core.mapping (NOT jakarta.persistence).
 * 2. Uses @Id from org.springframework.data.annotation (NOT jakarta.persistence).
 * 3. NO @Entity annotation — R2DBC doesn't use JPA's entity scanning.
 * 4. NO @GeneratedValue — R2DBC auto-generates IDs if the field is null on insert.
 * 5. NO @Column annotation needed — field names map to column names by convention.
 *    You can use @Column from org.springframework.data.relational.core.mapping if names differ.
 * 6. R2DBC doesn't support relationships (@OneToMany, @ManyToOne) directly.
 *    You need to handle joins manually or use separate queries.
 */
@Table("customer")
@Getter @Setter
public class Customer {

    @Id
    private Long id;
    private String email;
    private String pwd;
    private String role;

}
