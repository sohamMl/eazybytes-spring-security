package com.eazybytes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Customer entity for Section 8+ (full banking app).
 * Has more fields than Section 4's simpler version.
 */
@Table("customer")
@Getter @Setter
public class Customer {

    @Id
    @Column("customer_id")
    private Long id;

    private String name;
    private String email;

    @Column("mobile_number")
    private String mobileNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pwd;

    private String role;

    @Column("create_dt")
    @JsonIgnore
    private String createDt;
}
