package com.stepup.ecommerce.wallet.wallet;

import com.stepup.ecommerce.wallet.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long transactionId,
        TransactionType type,
        BigDecimal amount,
        LocalDateTime timestamp
) {}