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
 * Refund Entity
 *
 * Represents a refund transaction
 */
@Entity
@Table(name = "refunds", indexes = {
    @Index(name = "idx_refund_number", columnList = "refundNumber"),
    @Index(name = "idx_payment_id", columnList = "paymentId"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String refundNumber;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RefundStatus status;

    @Column(length = 255)
    private String externalRefundId;

    @Column(length = 1000)
    private String failureReason;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    public enum RefundStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * Mark refund as completed
     */
    public void markCompleted(String externalRefundId) {
        this.status = RefundStatus.COMPLETED;
        this.externalRefundId = externalRefundId;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark refund as failed
     */
    public void markFailed(String reason) {
        this.status = RefundStatus.FAILED;
        this.failureReason = reason;
    }
}
