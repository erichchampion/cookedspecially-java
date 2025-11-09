package com.cookedspecially.customerservice.exception;

/**
 * Exception thrown when customer is not found
 */
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
