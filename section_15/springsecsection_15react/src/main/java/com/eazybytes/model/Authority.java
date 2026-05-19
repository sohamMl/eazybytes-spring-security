package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Authority entity for R2DBC.
 *
 * KEY DIFFERENCE FROM JPA:
 * In JPA (section 9 MVC), Authority has @ManyToOne with Customer and @JoinColumn.
 * R2DBC does NOT support @ManyToOne, @OneToMany, or any relationship annotations.
 * Instead, we store the customer_id as a plain column and perform manual joins
 * (or separate queries) in the service layer.
 */
@Table("authorities")
@Getter @Setter
public class Authority {

    @Id
    private Long id;

    private String name;

    @Column("customer_id")
    private Long customerId;
}
