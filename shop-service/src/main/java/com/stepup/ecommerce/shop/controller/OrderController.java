package com.stepup.ecommerce.shop.controller;

import com.stepup.ecommerce.shop.order.OrderResponse;
import com.stepup.ecommerce.shop.security.JwtTokenService;
import com.stepup.ecommerce.shop.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtTokenService jwtTokenService;

    public OrderController(OrderService orderService, JwtTokenService jwtTokenService) {
        this.orderService = orderService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(orderService.checkout(userId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(orderService.getOrders(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId
    ) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }
}