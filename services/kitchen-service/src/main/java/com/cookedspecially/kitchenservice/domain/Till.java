package com.cookedspecially.kitchenservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Till/Cash Register entity
 * Represents a physical cash register at a fulfillment center
 */
@Entity
@Table(name = "tills", indexes = {
    @Index(name = "idx_fulfillment_center", columnList = "fulfillmentCenterId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_current_user", columnList = "currentUserId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Till {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Till name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Fulfillment center ID is required")
    @Column(nullable = false)
    private Long fulfillmentCenterId;

    @NotNull(message = "Restaurant ID is required")
    @Column(nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TillStatus status = TillStatus.CLOSED;

    @Column(precision = 10, scale = 2)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentBalance = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal expectedBalance = BigDecimal.ZERO;

    private LocalDateTime openedAt;

    private LocalDateTime closedAt;

    @Column(length = 100)
    private String currentUserId; // User who opened the till

    @Column(length = 100)
    private String currentUserName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add cash to till
     */
    public void addCash(BigDecimal amount) {
        this.currentBalance = this.currentBalance.add(amount);
        this.expectedBalance = this.expectedBalance.add(amount);
    }

    /**
     * Withdraw cash from till
     */
    public void withdrawCash(BigDecimal amount) {
        this.currentBalance = this.currentBalance.subtract(amount);
        this.expectedBalance = this.expectedBalance.subtract(amount);
    }

    /**
     * Record a sale (updates expected balance)
     */
    public void recordSale(BigDecimal amount) {
        this.expectedBalance = this.expectedBalance.add(amount);
    }

    /**
     * Open till for shift
     */
    public void open(BigDecimal openingBalance, String userId, String userName) {
        this.status = TillStatus.OPEN;
        this.openingBalance = openingBalance;
        this.currentBalance = openingBalance;
        this.expectedBalance = openingBalance;
        this.openedAt = LocalDateTime.now();
        this.currentUserId = userId;
        this.currentUserName = userName;
        this.closedAt = null;
    }

    /**
     * Close till for shift
     */
    public void close() {
        this.status = TillStatus.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    /**
     * Calculate variance between expected and actual balance
     */
    public BigDecimal getVariance() {
        return currentBalance.subtract(expectedBalance);
    }
}
