package com.stepup.ecommerce.shop.service;

import com.stepup.ecommerce.shop.client.InventoryClient;
import com.stepup.ecommerce.shop.client.InventoryProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class InventoryGatewayService {

    private final InventoryClient inventoryClient;

    public InventoryGatewayService(InventoryClient inventoryClient) {
        this.inventoryClient = inventoryClient;
    }

    @CircuitBreaker(name = "inventoryService", fallbackMethod = "createProductFallback")
    public void createProduct(InventoryProductDto request) {
        inventoryClient.createProduct(request);
    }

    private void createProductFallback(InventoryProductDto request, Throwable t) {
        System.out.println("WARNING: inventory-service unavailable, product " +
                request.productId() + " not synced. Reason: " + t.getMessage());
    }
}