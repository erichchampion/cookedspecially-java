package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Response DTO
 */
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private Long orderId;
    private Long customerId;
    private PaymentStatus status;
    private PaymentProvider provider;
    private BigDecimal amount;
    private BigDecimal refundedAmount;
    private String currency;
    private String description;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Constructors
    public PaymentResponse() {
    }

    public PaymentResponse(Long id,
                 String paymentNumber,
                 Long orderId,
                 Long customerId,
                 PaymentStatus status,
                 PaymentProvider provider,
                 BigDecimal amount,
                 BigDecimal refundedAmount,
                 String currency,
                 String description,
                 String failureReason,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 LocalDateTime completedAt) {
        this.id = id;
        this.paymentNumber = paymentNumber;
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.provider = provider;
        this.amount = amount;
        this.refundedAmount = refundedAmount;
        this.currency = currency;
        this.description = description;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }


    public static PaymentResponse fromEntity(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentNumber(payment.getPaymentNumber());
        response.setOrderId(payment.getOrderId());
        response.setCustomerId(payment.getCustomerId());
        response.setStatus(payment.getStatus());
        response.setProvider(payment.getProvider());
        response.setAmount(payment.getAmount());
        response.setRefundedAmount(payment.getRefundedAmount());
        response.setCurrency(payment.getCurrency());
        response.setDescription(payment.getDescription());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setCompletedAt(payment.getCompletedAt());
        return response;
    }
}
