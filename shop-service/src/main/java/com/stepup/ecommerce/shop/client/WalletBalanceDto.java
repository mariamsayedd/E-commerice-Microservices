package com.stepup.ecommerce.shop.client;

import java.math.BigDecimal;

public record WalletBalanceDto(Long walletId, BigDecimal balance, String currency) {}