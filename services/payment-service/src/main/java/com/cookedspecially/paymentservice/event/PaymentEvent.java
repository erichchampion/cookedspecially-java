package com.cookedspecially.paymentservice.event;

import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Event
 *
 * Published to SNS/SQS when payment lifecycle events occur
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEvent {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private PaymentEventType eventType;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("payment_number")
    private String paymentNumber;

    @JsonProperty("order_id")
    private Long orderId;

    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("status")
    private PaymentStatus status;

    @JsonProperty("provider")
    private PaymentProvider provider;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("refund_amount")
    private BigDecimal refundAmount;

    @JsonProperty("failure_reason")
    private String failureReason;

    public enum PaymentEventType {
        PAYMENT_INITIATED,
        PAYMENT_PROCESSING,
        PAYMENT_COMPLETED,
        PAYMENT_FAILED,
        PAYMENT_CANCELLED,
        REFUND_INITIATED,
        REFUND_COMPLETED,
        REFUND_FAILED
    }
}
