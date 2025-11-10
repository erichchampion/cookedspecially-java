package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a till is not found
 */
public class TillNotFoundException extends RuntimeException {
    public TillNotFoundException(String message) {
        super(message);
    }

    public TillNotFoundException(Long tillId) {
        super("Till not found with ID: " + tillId);
    }
}
