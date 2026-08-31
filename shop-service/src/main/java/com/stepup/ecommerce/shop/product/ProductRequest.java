package com.stepup.ecommerce.shop.product;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String imageUrl
) {}