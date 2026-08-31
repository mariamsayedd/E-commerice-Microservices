CREATE DATABASE IF NOT EXISTS walletdb;
USE walletdb;

CREATE TABLE users (
    user_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallets (
    wallet_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    balance         DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    currency        VARCHAR(10) NOT NULL DEFAULT 'USD',
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT uq_wallet_user UNIQUE (user_id)
);

CREATE TABLE wallet_transactions (
    transaction_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id       BIGINT NOT NULL,
    type            ENUM('DEPOSIT', 'WITHDRAWAL') NOT NULL,
    amount          DECIMAL(15,2) NOT NULL,
    timestamp       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id)
);

CREATE INDEX idx_wallet_user ON wallets(user_id);
CREATE INDEX idx_transaction_wallet ON wallet_transactions(wallet_id);
