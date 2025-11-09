package com.cookedspecially.orderservice.event;

import com.cookedspecially.orderservice.domain.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Order Event
 *
 * Published to SNS/SQS when order lifecycle events occur
 */
public class OrderEvent {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private OrderEventType eventType;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("status")
    private OrderStatus status;

    @JsonProperty("previous_status")
    private OrderStatus previousStatus;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("payment_status")
    private String paymentStatus;

    // Constructors
    public OrderEvent() {
    }

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public OrderEventType getEventType() {
        return eventType;
    }

    public void setEventType(OrderEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(OrderStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public enum OrderEventType {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        ORDER_STATUS_CHANGED,
        PAYMENT_STATUS_CHANGED,
        ORDER_DELIVERED
    }
}
