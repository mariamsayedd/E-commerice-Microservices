package com.stepup.ecommerce.wallet.auth;

public record LoginRequest(
        String email
        ,String password
) {}