package com.stepup.ecommerce.shop.category;

public record CategoryResponse(
        Long categoryId,
        String name,
        String description
) {}