package com.stepup.ecommerce.inventory.product;

public record ProductResponse(
        Long productId,
        String name,
        String sku
) {}