package dev.ordersync.integration;

public class IdempotencyConflictException extends RuntimeException {
    public IdempotencyConflictException() {
        super("Order reference already used with different order data");
    }
}
