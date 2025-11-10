package com.cookedspecially.reportingservice.exception;

/**
 * Exception thrown when date range is invalid.
 */
public class InvalidDateRangeException extends RuntimeException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
