package com.stepup.ecommerce.wallet.auth;

public record RegisterRequest(
        String username,
        String email,
        String password
) {}