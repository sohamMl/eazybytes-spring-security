-- Schema for Section 8+ (full banking app with H2)

CREATE TABLE IF NOT EXISTS customer (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(255),
    mobile_number VARCHAR(20),
    pwd VARCHAR(500),
    role VARCHAR(100),
    create_dt DATE
);

CREATE TABLE IF NOT EXISTS accounts (
    customer_id BIGINT NOT NULL,
    account_number BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_type VARCHAR(100),
    branch_address VARCHAR(200),
    create_dt DATE
);

CREATE TABLE IF NOT EXISTS account_transactions (
    transaction_id VARCHAR(200) PRIMARY KEY,
    account_number BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    transaction_dt DATE,
    transaction_summary VARCHAR(200),
    transaction_type VARCHAR(100),
    transaction_amt INT,
    closing_balance INT,
    create_dt DATE
);

CREATE TABLE IF NOT EXISTS loans (
    loan_number BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    start_dt DATE,
    loan_type VARCHAR(100),
    total_loan INT,
    amount_paid INT,
    outstanding_amount INT,
    create_dt DATE
);

CREATE TABLE IF NOT EXISTS cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    card_number VARCHAR(100),
    card_type VARCHAR(100),
    total_limit INT,
    amount_used INT,
    available_amount INT,
    create_dt DATE
);

CREATE TABLE IF NOT EXISTS notice_details (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    notice_summary VARCHAR(200),
    notice_details VARCHAR(500),
    notic_beg_dt DATE,
    notic_end_dt DATE,
    create_dt DATE,
    update_dt DATE
);

CREATE TABLE IF NOT EXISTS contact_messages (
    contact_id VARCHAR(50) PRIMARY KEY,
    contact_name VARCHAR(100),
    contact_email VARCHAR(255),
    subject VARCHAR(500),
    message VARCHAR(2000),
    create_dt DATE
);
