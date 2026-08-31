package com.stepup.ecommerce.shop.controller;

import com.stepup.ecommerce.shop.cart.AddCartItemRequest;
import com.stepup.ecommerce.shop.cart.CartResponse;
import com.stepup.ecommerce.shop.cart.UpdateCartItemRequest;
import com.stepup.ecommerce.shop.security.JwtTokenService;
import com.stepup.ecommerce.shop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final JwtTokenService jwtTokenService;

    public CartController(CartService cartService, JwtTokenService jwtTokenService) {
        this.cartService = cartService;
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AddCartItemRequest request
    ) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> updateItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemRequest request
    ) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(cartService.updateItem(userId, cartItemId, request));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long cartItemId
    ) {
        Long userId = jwtTokenService.extractUserId(authHeader);
        return ResponseEntity.ok(cartService.removeItem(userId, cartItemId));
    }
}