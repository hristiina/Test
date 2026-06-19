package org.example.auth;

import java.time.Instant;

public record AuthErrorResponse(
        String error,
        String message,
        Instant timestamp
) {
}
