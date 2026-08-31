package com.stepup.ecommerce.inventory.service;

import com.stepup.ecommerce.inventory.entity.Inventory;
import com.stepup.ecommerce.inventory.entity.Product;
import com.stepup.ecommerce.inventory.inventory.AdjustQuantityRequest;
import com.stepup.ecommerce.inventory.inventory.InventoryRequest;
import com.stepup.ecommerce.inventory.inventory.InventoryResponse;
import com.stepup.ecommerce.inventory.repository.InventoryRepository;
import com.stepup.ecommerce.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public InventoryResponse create(InventoryRequest request) {

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (inventoryRepository.findByProduct(product).isPresent()) {
            throw new RuntimeException("Inventory already exists for this product");
        }

        if (request.quantityAvailable() == null || request.quantityAvailable() < 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantityAvailable(request.quantityAvailable());
        inventory.setWarehouseLocation(request.warehouseLocation());
        inventory.setUpdatedAt(LocalDateTime.now());

        inventory = inventoryRepository.save(inventory);

        return toResponse(inventory);
    }

    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for this product"));
        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse increase(Long productId, AdjustQuantityRequest request) {

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for this product"));

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + request.quantity());
        inventory.setUpdatedAt(LocalDateTime.now());

        inventory = inventoryRepository.save(inventory);

        return toResponse(inventory);
    }

    @Transactional
    public InventoryResponse decrease(Long productId, AdjustQuantityRequest request) {

        if (request.quantity() == null || request.quantity() <= 0) {
            throw new RuntimeException("Invalid quantity");
        }

        Inventory inventory = inventoryRepository.findByProduct_ProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for this product"));

        if (inventory.getQuantityAvailable() < request.quantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - request.quantity());
        inventory.setUpdatedAt(LocalDateTime.now());

        inventory = inventoryRepository.save(inventory);

        return toResponse(inventory);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getInventoryId(),
                inventory.getProduct().getProductId(),
                inventory.getProduct().getName(),
                inventory.getQuantityAvailable(),
                inventory.getWarehouseLocation(),
                inventory.getUpdatedAt()
        );
    }
}