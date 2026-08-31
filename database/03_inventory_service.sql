CREATE DATABASE IF NOT EXISTS inventorydb;
USE inventorydb;

CREATE TABLE products (
    product_id      BIGINT PRIMARY KEY,            -- cross-service ref -> shopdb.products.product_id
    name            VARCHAR(150) NOT NULL,
    sku             VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE inventory (
    inventory_id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id          BIGINT NOT NULL,
    quantity_available   INT NOT NULL DEFAULT 0,
    warehouse_location   VARCHAR(100),
    updated_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(product_id),
    CONSTRAINT uq_inventory_product UNIQUE (product_id)   -- one inventory row per product
);

CREATE INDEX idx_inventory_product ON inventory(product_id);
