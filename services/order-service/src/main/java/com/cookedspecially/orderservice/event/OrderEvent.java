package com.cookedspecially.orderservice.event;

import com.cookedspecially.orderservice.domain.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base Order Event
 *
 * Published to SNS/SQS when order lifecycle events occur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public enum OrderEventType {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        ORDER_STATUS_CHANGED,
        PAYMENT_STATUS_CHANGED,
        ORDER_DELIVERED
    }
}
