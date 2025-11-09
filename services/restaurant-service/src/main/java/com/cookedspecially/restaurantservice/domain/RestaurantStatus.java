package com.cookedspecially.restaurantservice.domain;

/**
 * Restaurant Status Enum
 */
public enum RestaurantStatus {
    /**
     * Restaurant is pending approval
     */
    PENDING_APPROVAL,

    /**
     * Restaurant is active and accepting orders
     */
    ACTIVE,

    /**
     * Restaurant is temporarily closed
     */
    TEMPORARILY_CLOSED,

    /**
     * Restaurant is permanently closed
     */
    PERMANENTLY_CLOSED,

    /**
     * Restaurant is suspended
     */
    SUSPENDED;

    /**
     * Check if restaurant is accepting orders
     */
    public boolean isAcceptingOrders() {
        return this == ACTIVE;
    }

    /**
     * Check if restaurant can be edited
     */
    public boolean canBeEdited() {
        return this != PERMANENTLY_CLOSED;
    }
}
