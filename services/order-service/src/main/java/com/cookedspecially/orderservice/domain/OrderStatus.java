package com.cookedspecially.orderservice.domain;

/**
 * Order Status Enum
 *
 * Represents the lifecycle states of an order.
 */
public enum OrderStatus {
    PENDING("Order received and pending confirmation"),
    CONFIRMED("Order confirmed by restaurant"),
    PREPARING("Order is being prepared"),
    READY("Order is ready for pickup/delivery"),
    OUT_FOR_DELIVERY("Order is out for delivery"),
    DELIVERED("Order has been delivered"),
    CANCELLED("Order has been cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == PREPARING || newStatus == CANCELLED;
            case PREPARING -> newStatus == READY || newStatus == CANCELLED;
            case READY -> newStatus == OUT_FOR_DELIVERY || newStatus == DELIVERED || newStatus == CANCELLED;
            case OUT_FOR_DELIVERY -> newStatus == DELIVERED || newStatus == CANCELLED;
            case DELIVERED, CANCELLED -> false; // Terminal states
        };
    }
}
