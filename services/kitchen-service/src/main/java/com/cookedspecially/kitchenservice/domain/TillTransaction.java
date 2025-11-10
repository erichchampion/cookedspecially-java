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
 * Till Transaction entity
 * Tracks all cash movements in a till
 */
@Entity
@Table(name = "till_transactions", indexes = {
    @Index(name = "idx_till_id", columnList = "tillId"),
    @Index(name = "idx_transaction_type", columnList = "transactionType"),
    @Index(name = "idx_transaction_date", columnList = "transactionDate"),
    @Index(name = "idx_order_id", columnList = "orderId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Till ID is required")
    @Column(nullable = false)
    private Long tillId;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType transactionType;

    @NotNull(message = "Amount is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal balanceAfter;

    private Long orderId; // Reference to order (if applicable)

    @Column(length = 100)
    private String paymentMethod; // cash, card, online, etc.

    @Column(length = 1000)
    private String notes;

    @Column(length = 200)
    private String reference; // Reference number (invoice, receipt, etc.)

    @NotNull
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    @Column(length = 100)
    private String performedBy; // User who performed the transaction

    @Column(length = 100)
    private String performedByName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
        createdAt = LocalDateTime.now();
    }
}
