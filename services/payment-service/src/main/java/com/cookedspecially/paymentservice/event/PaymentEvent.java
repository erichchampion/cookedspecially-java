package com.cookedspecially.paymentservice.event;

import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Event
 *
 * Published to SNS/SQS when payment lifecycle events occur
 */
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

    // Getters and Setters
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public PaymentEventType getEventType() {
        return eventType;
    }

    public void setEventType(PaymentEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

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
