package com.cookedspecially.restaurantservice.exception;

/**
 * Exception thrown when restaurant state transition is invalid
 */
public class InvalidRestaurantStateException extends RuntimeException {
    private final Long restaurantId;
    private final String currentState;
    private final String requestedState;

    public InvalidRestaurantStateException(Long restaurantId, String currentState, String requestedState) {
        super(String.format("Invalid state transition for restaurant %d: from %s to %s",
            restaurantId, currentState, requestedState));
        this.restaurantId = restaurantId;
        this.currentState = currentState;
        this.requestedState = requestedState;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getRequestedState() {
        return requestedState;
    }
}
