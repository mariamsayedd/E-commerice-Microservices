package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.client.AmountDto;
import com.stepup.ecommerce.shop.client.WalletBalanceDto;
import com.stepup.ecommerce.shop.client.WalletClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletGatewayService {

    private final WalletClient walletClient;

    public WalletGatewayService(WalletClient walletClient) {
        this.walletClient = walletClient;
    }

    @CircuitBreaker(name = "walletService", fallbackMethod = "withdrawFallback")
    public WalletBalanceDto withdraw(BigDecimal amount) {
        return walletClient.withdraw(new AmountDto(amount));
    }

    private WalletBalanceDto withdrawFallback(BigDecimal amount, Throwable t) {
        throw new RuntimeException("Wallet service is currently unavailable, please try again later");
    }
}