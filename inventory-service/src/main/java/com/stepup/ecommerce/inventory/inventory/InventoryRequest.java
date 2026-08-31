package com.stepup.ecommerce.inventory.inventory;

public record InventoryRequest(
        Long productId,
        Integer quantityAvailable,
        String warehouseLocation
) {}