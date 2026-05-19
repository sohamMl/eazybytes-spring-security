package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("accounts")
@Getter @Setter
public class Accounts {

    @Column("customer_id")
    private long customerId;

    @Id
    @Column("account_number")
    private long accountNumber;

    @Column("account_type")
    private String accountType;

    @Column("branch_address")
    private String branchAddress;

    @Column("create_dt")
    private String createDt;
}
