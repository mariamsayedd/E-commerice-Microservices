package com.stepup.ecommerce.shop.cart;

public record AddCartItemRequest(Long productId, Integer quantity) {}