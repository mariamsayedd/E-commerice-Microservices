package com.stepup.ecommerce.inventory.repository;

import com.stepup.ecommerce.inventory.entity.Inventory;
import com.stepup.ecommerce.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProduct(Product product);

    Optional<Inventory> findByProduct_ProductId(Long productId);
}