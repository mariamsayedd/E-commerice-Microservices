package com.stepup.ecommerce.shop.controller;

import com.stepup.ecommerce.shop.payment.PaymentRequest;
import com.stepup.ecommerce.shop.payment.PaymentResponse;
import com.stepup.ecommerce.shop.security.JwtTokenService;
import com.stepup.ecommerce.shop.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders/{orderId}/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final JwtTokenService jwtTokenService;

    public PaymentController(PaymentService paymentService, JwtTokenService jwtTokenService) {
        this.paymentService = paymentService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long orderId,
            @RequestBody PaymentRequest request
    ) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(paymentService.pay(userId, orderId, request));
    }
}