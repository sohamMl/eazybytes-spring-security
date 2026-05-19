package com.eazybytes.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("contact_messages")
@Getter @Setter
public class Contact {

    @Id
    @Column("contact_id")
    private String contactId;

    @Column("contact_name")
    private String contactName;

    @Column("contact_email")
    private String contactEmail;

    private String subject;
    private String message;

    @Column("create_dt")
    private String createDt;
}
