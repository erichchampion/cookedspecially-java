package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a delivery area is not found
 */
public class DeliveryAreaNotFoundException extends RuntimeException {
    public DeliveryAreaNotFoundException(String message) {
        super(message);
    }

    public DeliveryAreaNotFoundException(Long areaId) {
        super("Delivery area not found with ID: " + areaId);
    }
}
