package com.stepup.ecommerce.shop.controller;

import com.stepup.ecommerce.shop.security.JwtTokenService;
import com.stepup.ecommerce.shop.service.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/images")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;
    private final JwtTokenService jwtTokenService;

    public ImageUploadController(ImageUploadService imageUploadService, JwtTokenService jwtTokenService) {
        this.imageUploadService = imageUploadService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> upload(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file
    ) {
        jwtTokenService.requireAdmin(authHeader);
        String url = imageUploadService.upload(file);
        return ResponseEntity.ok(Map.of("imageUrl", url));
    }
}