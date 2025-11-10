package com.cookedspecially.kitchenservice.exception;

/**
 * Exception thrown when a delivery boy is not found
 */
public class DeliveryBoyNotFoundException extends RuntimeException {
    public DeliveryBoyNotFoundException(String message) {
        super(message);
    }

    public DeliveryBoyNotFoundException(Long deliveryBoyId) {
        super("Delivery boy not found with ID: " + deliveryBoyId);
    }
}
