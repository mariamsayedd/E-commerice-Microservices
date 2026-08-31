package com.stepup.ecommerce.shop.repository;

import com.stepup.ecommerce.shop.entity.Order;
import com.stepup.ecommerce.shop.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrder(Order order);
}