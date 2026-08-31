package com.stepup.ecommerce.shop.repository;

import com.stepup.ecommerce.shop.entity.Order;
import com.stepup.ecommerce.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}