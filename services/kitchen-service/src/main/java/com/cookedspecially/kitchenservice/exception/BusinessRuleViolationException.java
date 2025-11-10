package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a business rule is violated
 */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
