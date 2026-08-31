package com.stepup.ecommerce.inventory.repository;

import com.stepup.ecommerce.inventory.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySku(String sku);
}