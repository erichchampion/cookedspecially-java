package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a seating table is not found
 */
public class SeatingTableNotFoundException extends RuntimeException {
    public SeatingTableNotFoundException(String message) {
        super(message);
    }

    public SeatingTableNotFoundException(Long tableId) {
        super("Seating table not found with ID: " + tableId);
    }
}
