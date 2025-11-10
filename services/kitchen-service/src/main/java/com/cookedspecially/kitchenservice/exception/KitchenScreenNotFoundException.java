package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a kitchen screen is not found
 */
public class KitchenScreenNotFoundException extends RuntimeException {
    public KitchenScreenNotFoundException(String message) {
        super(message);
    }

    public KitchenScreenNotFoundException(Long screenId) {
        super("Kitchen screen not found with ID: " + screenId);
    }
}
