package com.stepup.ecommerce.shop.repository;

import com.stepup.ecommerce.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByName(String name);
}