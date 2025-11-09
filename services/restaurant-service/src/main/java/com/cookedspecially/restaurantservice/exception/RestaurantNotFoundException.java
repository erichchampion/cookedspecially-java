package com.cookedspecially.restaurantservice.exception;

public class RestaurantNotFoundException extends RuntimeException {
    private final Long restaurantId;

    public RestaurantNotFoundException(Long restaurantId) {
        super("Restaurant not found with ID: " + restaurantId);
        this.restaurantId = restaurantId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }
}
