package com.cookedspecially.kitchenservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Till Handover entity
 * Tracks till handovers between shifts
 */
@Entity
@Table(name = "till_handovers", indexes = {
    @Index(name = "idx_till_id", columnList = "tillId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_handover_date", columnList = "handoverDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillHandover {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Till ID is required")
    @Column(nullable = false)
    private Long tillId;

    @Column(length = 100)
    private String fromUserId;

    @Column(length = 100)
    private String fromUserName;

    @Column(length = 100)
    private String toUserId;

    @Column(length = 100)
    private String toUserName;

    @Column(precision = 10, scale = 2)
    private BigDecimal expectedBalance;

    @Column(precision = 10, scale = 2)
    private BigDecimal actualBalance;

    @Column(precision = 10, scale = 2)
    private BigDecimal variance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HandoverStatus status = HandoverStatus.PENDING;

    @Column(length = 1000)
    private String notes;

    @Column(length = 1000)
    private String rejectionReason;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime handoverDate;

    private LocalDateTime approvedAt;

    @Column(length = 100)
    private String approvedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (handoverDate == null) {
            handoverDate = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Approve handover
     */
    public void approve(String approver) {
        this.status = HandoverStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approver;
    }

    /**
     * Reject handover
     */
    public void reject(String reason) {
        this.status = HandoverStatus.REJECTED;
        this.rejectionReason = reason;
    }

    /**
     * Complete handover
     */
    public void complete() {
        this.status = HandoverStatus.COMPLETED;
    }
}
