package com.cookedspecially.orderservice.exception;

/**
 * Exception thrown when order validation fails
 */
public class OrderValidationException extends RuntimeException {

    private final String field;

    public OrderValidationException(String message) {
        super(message);
        this.field = null;
    }

    public OrderValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
