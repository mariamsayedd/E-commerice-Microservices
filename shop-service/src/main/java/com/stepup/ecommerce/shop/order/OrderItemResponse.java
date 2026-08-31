package com.stepup.ecommerce.shop.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {}