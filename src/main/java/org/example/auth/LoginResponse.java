package org.example.auth;

public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds
) {
}
