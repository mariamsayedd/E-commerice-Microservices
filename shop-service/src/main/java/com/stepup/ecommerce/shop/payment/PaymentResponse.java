package com.stepup.ecommerce.shop.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long paymentId,
        Long orderId,
        BigDecimal amount,
        String status,
        LocalDateTime paymentDate
) {}