package com.cookedspecially.orderservice.exception;

import com.cookedspecially.orderservice.domain.OrderStatus;

/**
 * Exception thrown when attempting an invalid order state transition
 */
public class InvalidOrderStateException extends RuntimeException {

    private final OrderStatus currentStatus;
    private final OrderStatus requestedStatus;

    public InvalidOrderStateException(OrderStatus currentStatus, OrderStatus requestedStatus) {
        super(String.format("Cannot transition order from %s to %s", currentStatus, requestedStatus));
        this.currentStatus = currentStatus;
        this.requestedStatus = requestedStatus;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public OrderStatus getRequestedStatus() {
        return requestedStatus;
    }
}
