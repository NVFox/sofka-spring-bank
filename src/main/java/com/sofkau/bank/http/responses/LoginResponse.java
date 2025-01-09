package com.sofkau.bank.http.responses;

public record LoginResponse(
        String token,
        long expiresIn) {
}
