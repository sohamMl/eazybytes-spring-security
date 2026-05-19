package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("loans")
@Getter @Setter
public class Loans {

    @Id
    @Column("loan_number")
    private long loanNumber;

    @Column("customer_id")
    private long customerId;

    @Column("start_dt")
    private String startDt;

    @Column("loan_type")
    private String loanType;

    @Column("total_loan")
    private int totalLoan;

    @Column("amount_paid")
    private int amountPaid;

    @Column("outstanding_amount")
    private int outstandingAmount;

    @Column("create_dt")
    private String createDt;
}
