package com.stepup.ecommerce.shop.controller;

import com.stepup.ecommerce.shop.category.CategoryRequest;
import com.stepup.ecommerce.shop.category.CategoryResponse;
import com.stepup.ecommerce.shop.security.JwtTokenService;
import com.stepup.ecommerce.shop.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final JwtTokenService jwtTokenService;

    public CategoryController(CategoryService categoryService, JwtTokenService jwtTokenService) {
        this.categoryService = categoryService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CategoryRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(categoryService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getById(categoryId));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId,
            @RequestBody CategoryRequest request
    ) {
        jwtTokenService.requireAdmin(authHeader);
        return ResponseEntity.ok(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long categoryId
    ) {
        jwtTokenService.requireAdmin(authHeader);
        categoryService.delete(categoryId);
        return ResponseEntity.noContent().build();
    }
}