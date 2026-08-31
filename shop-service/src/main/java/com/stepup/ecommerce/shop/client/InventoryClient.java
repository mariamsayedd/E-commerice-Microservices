package com.stepup.ecommerce.shop.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", configuration = FeignClientConfig.class)
public interface InventoryClient {

    @PostMapping("/products")
    void createProduct(@RequestBody InventoryProductDto request);
}