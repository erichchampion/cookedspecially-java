package com.cookedspecially.kitchenservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Seating Table entity
 * Represents a physical table for dine-in service
 */
@Entity
@Table(name = "seating_tables", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_fulfillment_center", columnList = "fulfillmentCenterId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_table_number", columnList = "restaurantId,tableNumber")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Table number is required")
    @Column(nullable = false, length = 50)
    private String tableNumber;

    @Column(length = 100)
    private String name; // Optional friendly name (e.g., "Corner Table", "VIP 1")

    @NotNull(message = "Restaurant ID is required")
    @Column(nullable = false)
    private Long restaurantId;

    @NotNull(message = "Fulfillment center ID is required")
    @Column(nullable = false)
    private Long fulfillmentCenterId;

    @Column(nullable = false)
    private Integer capacity = 4; // Number of seats

    @Column(length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, OCCUPIED, RESERVED, CLEANING

    @Column(length = 100)
    private String section; // Section or area (e.g., "Patio", "Main Hall", "Private Room")

    @Column(length = 100)
    private String qrCode; // QR code for mobile ordering

    private Long currentOrderId; // Current order on this table

    private LocalDateTime occupiedSince;

    private Boolean active = true;

    @Column(length = 500)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 100)
    private String createdBy;

    @Column(length = 100)
    private String updatedBy;

    private LocalDateTime deletedAt; // Soft delete

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
     * Occupy table with order
     */
    public void occupy(Long orderId) {
        this.status = "OCCUPIED";
        this.currentOrderId = orderId;
        this.occupiedSince = LocalDateTime.now();
    }

    /**
     * Release table (order completed)
     */
    public void release() {
        this.status = "AVAILABLE";
        this.currentOrderId = null;
        this.occupiedSince = null;
    }

    /**
     * Reserve table
     */
    public void reserve() {
        this.status = "RESERVED";
    }

    /**
     * Mark table for cleaning
     */
    public void markForCleaning() {
        this.status = "CLEANING";
        this.currentOrderId = null;
        this.occupiedSince = null;
    }

    /**
     * Check if table is available
     */
    public boolean isAvailable() {
        return active && "AVAILABLE".equals(status) && deletedAt == null;
    }

    /**
     * Soft delete
     */
    public void delete() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }
}
