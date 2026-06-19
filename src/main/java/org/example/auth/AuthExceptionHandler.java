package org.example.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Scoped to AuthController only, so the catch-all 500 handler below doesn't
 * swallow errors from unrelated controllers added in the future.
 */
@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

    @ExceptionHandler({BadCredentialsException.class, DisabledException.class, UsernameNotFoundException.class})
    public ResponseEntity<AuthErrorResponse> handleAuthenticationFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthErrorResponse("unauthorized", "Invalid username or password", Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthErrorResponse> handleValidationFailure(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AuthErrorResponse("invalid_request", message, Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AuthErrorResponse> handleUnexpectedError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new AuthErrorResponse("internal_error", "An unexpected error occurred", Instant.now()));
    }
}
