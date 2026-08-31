package com.stepup.ecommerce.inventory.controller;

import com.stepup.ecommerce.inventory.inventory.AdjustQuantityRequest;
import com.stepup.ecommerce.inventory.inventory.InventoryRequest;
import com.stepup.ecommerce.inventory.inventory.InventoryResponse;
import com.stepup.ecommerce.inventory.security.JwtTokenService;
import com.stepup.ecommerce.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final JwtTokenService jwtTokenService;

    public InventoryController(InventoryService inventoryService, JwtTokenService jwtTokenService) {
        this.inventoryService = inventoryService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody InventoryRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(inventoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAll() {
        return ResponseEntity.ok(inventoryService.getAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    @PostMapping("/{productId}/increase")
    public ResponseEntity<InventoryResponse> increase(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId,
            @RequestBody AdjustQuantityRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(inventoryService.increase(productId, request));
    }

    @PostMapping("/{productId}/decrease")
    public ResponseEntity<InventoryResponse> decrease(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId,
            @RequestBody AdjustQuantityRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(inventoryService.decrease(productId, request));
    }
}