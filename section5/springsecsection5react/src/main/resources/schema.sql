-- Schema for H2 in-memory database (R2DBC)
-- This file is automatically executed by Spring Boot on startup

CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255),
    pwd VARCHAR(255),
    role VARCHAR(100)
);
