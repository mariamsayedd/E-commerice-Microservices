package com.stepup.ecommerce.shop.repository;

import com.stepup.ecommerce.shop.entity.Cart;
import com.stepup.ecommerce.shop.entity.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
}