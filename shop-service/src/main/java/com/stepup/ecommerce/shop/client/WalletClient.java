package com.stepup.ecommerce.shop.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "wallet-service", configuration = FeignClientConfig.class)
public interface WalletClient {

    @PostMapping("/wallet/withdraw")
    WalletBalanceDto withdraw(@RequestBody AmountDto request);
}