package dev.ordersync.api;

import dev.ordersync.integration.IdempotencyConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, String>> duplicateConflict(IdempotencyConflictException ignored) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "ORDER_REFERENCE_CONFLICT"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> providerFailure(IllegalStateException ignored) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", "UPSTREAM_FAILURE"));
    }
}
