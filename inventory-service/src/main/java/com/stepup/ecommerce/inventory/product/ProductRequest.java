package com.stepup.ecommerce.inventory.product;

public record ProductRequest(
        Long productId,
        String name,
        String sku
) {}