package com.cookedspecially.paymentservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
