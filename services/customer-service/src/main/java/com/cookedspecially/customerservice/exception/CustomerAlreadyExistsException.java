package com.cookedspecially.customerservice.exception;

/**
 * Exception thrown when customer already exists
 */
public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}
