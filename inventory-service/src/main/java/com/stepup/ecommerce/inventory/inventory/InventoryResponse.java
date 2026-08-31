package com.stepup.ecommerce.inventory.inventory;

import java.time.LocalDateTime;

public record InventoryResponse(
        Long inventoryId,
        Long productId,
        String productName,
        Integer quantityAvailable,
        String warehouseLocation,
        LocalDateTime updatedAt
) {}