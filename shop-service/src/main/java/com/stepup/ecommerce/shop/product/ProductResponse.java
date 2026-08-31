package com.stepup.ecommerce.shop.product;

import java.math.BigDecimal;

public record ProductResponse(
        Long productId,
        String name,
        String description,
        BigDecimal price,
        Long categoryId,
        String categoryName,
        String imageUrl
) {}