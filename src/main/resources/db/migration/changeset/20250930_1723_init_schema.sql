--liquibase formatted sql

--changeset polina:20250930_1723_init_schema
CREATE TABLE "user" (
    id UUID PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE card (
      id UUID PRIMARY KEY,
      number VARCHAR(255),
      user_id UUID,
      expiration_date VARCHAR(7),
      status VARCHAR(50),
      balance NUMERIC(38,2) DEFAULT 0.00
);

ALTER TABLE card ADD CONSTRAINT fk_card_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE;