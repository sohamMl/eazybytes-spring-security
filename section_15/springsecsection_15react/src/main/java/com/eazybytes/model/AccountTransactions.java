package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("account_transactions")
@Getter @Setter
public class AccountTransactions {

    @Id
    @Column("transaction_id")
    private String transactionId;

    @Column("account_number")
    private long accountNumber;

    @Column("customer_id")
    private long customerId;

    @Column("transaction_dt")
    private String transactionDt;

    @Column("transaction_summary")
    private String transactionSummary;

    @Column("transaction_type")
    private String transactionType;

    @Column("transaction_amt")
    private int transactionAmt;

    @Column("closing_balance")
    private int closingBalance;

    @Column("create_dt")
    private String createDt;
}
