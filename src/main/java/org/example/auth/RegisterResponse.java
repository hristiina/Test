package org.example.auth;

public record RegisterResponse(
        Long id,
        String username,
        String email
) {
}
