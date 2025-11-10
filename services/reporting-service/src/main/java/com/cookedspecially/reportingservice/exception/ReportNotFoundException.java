package com.cookedspecially.reportingservice.exception;

/**
 * Exception thrown when a report is not found.
 */
public class ReportNotFoundException extends RuntimeException {
    public ReportNotFoundException(String message) {
        super(message);
    }
}
