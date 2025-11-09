package com.cookedspecially.orderservice.exception;

/**
 * Exception thrown when an order is not found
 */
public class OrderNotFoundException extends RuntimeException {

    private final Long orderId;
    private final String orderNumber;

    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
        this.orderId = orderId;
        this.orderNumber = null;
    }

    public OrderNotFoundException(String orderNumber) {
        super("Order not found with order number: " + orderNumber);
        this.orderId = null;
        this.orderNumber = orderNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
}
