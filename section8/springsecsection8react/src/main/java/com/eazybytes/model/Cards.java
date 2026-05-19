package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cards")
@Getter @Setter
public class Cards {

    @Id
    @Column("card_id")
    private long cardId;

    @Column("customer_id")
    private long customerId;

    @Column("card_number")
    private String cardNumber;

    @Column("card_type")
    private String cardType;

    @Column("total_limit")
    private int totalLimit;

    @Column("amount_used")
    private int amountUsed;

    @Column("available_amount")
    private int availableAmount;

    @Column("create_dt")
    private String createDt;
}
