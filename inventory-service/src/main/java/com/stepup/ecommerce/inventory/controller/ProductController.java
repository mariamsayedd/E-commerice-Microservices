package com.stepup.ecommerce.inventory.controller;

import com.stepup.ecommerce.inventory.product.ProductRequest;
import com.stepup.ecommerce.inventory.product.ProductResponse;
import com.stepup.ecommerce.inventory.security.JwtTokenService;
import com.stepup.ecommerce.inventory.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final JwtTokenService jwtTokenService;

    public ProductController(ProductService productService, JwtTokenService jwtTokenService) {
        this.productService = productService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ProductRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(productService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId
    ) {
        jwtTokenService.requireAdmin(authHeader);
        productService.delete(productId);
        return ResponseEntity.noContent().build();
    }
}