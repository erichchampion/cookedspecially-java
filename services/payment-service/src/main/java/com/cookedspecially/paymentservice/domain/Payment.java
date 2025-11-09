package com.cookedspecially.paymentservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Entity
 *
 * Represents a payment transaction in the system
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_number", columnList = "paymentNumber"),
    @Index(name = "idx_order_id", columnList = "orderId"),
    @Index(name = "idx_customer_id", columnList = "customerId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_provider", columnList = "provider")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String paymentNumber;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal refundedAmount;

    @Column(length = 3)
    private String currency;

    @Column(length = 255)
    private String externalTransactionId;

    @Column(length = 255)
    private String externalCustomerId;

    @Column(length = 500)
    private String description;

    @Column(length = 1000)
    private String failureReason;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private LocalDateTime refundedAt;

    // Constructors
    public Payment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public void setRefundedAmount(BigDecimal refundedAmount) {
        this.refundedAmount = refundedAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExternalTransactionId() {
        return externalTransactionId;
    }

    public void setExternalTransactionId(String externalTransactionId) {
        this.externalTransactionId = externalTransactionId;
    }

    public String getExternalCustomerId() {
        return externalCustomerId;
    }

    public void setExternalCustomerId(String externalCustomerId) {
        this.externalCustomerId = externalCustomerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(LocalDateTime refundedAt) {
        this.refundedAt = refundedAt;
    }

    /**
     * Check if payment can be refunded
     */
    public boolean canBeRefunded() {
        return status.canBeRefunded() &&
               refundedAmount.compareTo(amount) < 0;
    }

    /**
     * Get remaining refundable amount
     */
    public BigDecimal getRefundableAmount() {
        return amount.subtract(refundedAmount);
    }

    /**
     * Check if payment is fully refunded
     */
    public boolean isFullyRefunded() {
        return refundedAmount.compareTo(amount) >= 0;
    }

    /**
     * Check if payment is partially refunded
     */
    public boolean isPartiallyRefunded() {
        return refundedAmount.compareTo(BigDecimal.ZERO) > 0 &&
               refundedAmount.compareTo(amount) < 0;
    }

    /**
     * Mark payment as completed
     */
    public void markCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.externalTransactionId = transactionId;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark payment as failed
     */
    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Add refund amount
     */
    public void addRefundAmount(BigDecimal amount) {
        this.refundedAmount = this.refundedAmount.add(amount);
        if (isFullyRefunded()) {
            this.status = PaymentStatus.REFUNDED;
            this.refundedAt = LocalDateTime.now();
        }
    }
}
