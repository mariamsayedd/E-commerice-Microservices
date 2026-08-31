package com.stepup.ecommerce.wallet.wallet;

import java.math.BigDecimal;

public record WalletResponse(
        Long walletId,
        BigDecimal balance,
        String currency
) {}