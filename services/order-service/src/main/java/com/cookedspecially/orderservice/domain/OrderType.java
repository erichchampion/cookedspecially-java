package com.cookedspecially.orderservice.domain;

/**
 * Order Type Enum
 *
 * Represents the type/method of order fulfillment.
 */
public enum OrderType {
    DELIVERY("Delivery to customer address"),
    PICKUP("Customer pickup from restaurant"),
    DINE_IN("Dine-in at restaurant");

    private final String description;

    OrderType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
