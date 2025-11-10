package com.cookedspecially.kitchenservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Delivery Boy/Personnel entity
 * Represents delivery personnel for order delivery
 */
@Entity
@Table(name = "delivery_boys", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_phone", columnList = "phone")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Column(nullable = false, length = 20, unique = true)
    private String phone;

    @Email
    @Column(length = 100)
    private String email;

    @NotNull(message = "Restaurant ID is required")
    @Column(nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DeliveryBoyStatus status = DeliveryBoyStatus.AVAILABLE;

    @Column(length = 50)
    private String vehicleType; // Bike, Scooter, Car, etc.

    @Column(length = 50)
    private String vehicleNumber;

    @Column(length = 100)
    private String licenseNumber;

    private Integer currentDeliveryCount = 0; // Active deliveries

    private Integer totalDeliveriesCompleted = 0;

    private Double averageRating = 0.0;

    private Integer ratingCount = 0;

    @Column(length = 500)
    private String notes;

    private Boolean active = true;

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
     * Assign delivery
     */
    public void assignDelivery() {
        this.currentDeliveryCount++;
        this.status = DeliveryBoyStatus.ON_DELIVERY;
    }

    /**
     * Complete delivery
     */
    public void completeDelivery() {
        if (this.currentDeliveryCount > 0) {
            this.currentDeliveryCount--;
        }
        this.totalDeliveriesCompleted++;
        if (this.currentDeliveryCount == 0) {
            this.status = DeliveryBoyStatus.AVAILABLE;
        }
    }

    /**
     * Update rating
     */
    public void updateRating(double rating) {
        double totalRating = this.averageRating * this.ratingCount;
        this.ratingCount++;
        this.averageRating = (totalRating + rating) / this.ratingCount;
    }

    /**
     * Check if available for delivery
     */
    public boolean isAvailable() {
        return this.active && this.status == DeliveryBoyStatus.AVAILABLE;
    }

    /**
     * Soft delete
     */
    public void delete() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.status = DeliveryBoyStatus.INACTIVE;
    }
}
