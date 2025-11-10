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
 * Delivery Area entity
 * Defines service areas for delivery with associated charges
 */
@Entity
@Table(name = "delivery_areas", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Area name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @NotNull(message = "Restaurant ID is required")
    @Column(nullable = false)
    private Long restaurantId;

    @NotNull(message = "Delivery charge is required")
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal deliveryCharge;

    @Column(precision = 8, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(precision = 8, scale = 2)
    private BigDecimal freeDeliveryAbove; // Free delivery if order amount exceeds this

    private Integer estimatedDeliveryTime; // In minutes

    @Column(length = 20)
    private String zipCode;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 50)
    private String country;

    private Boolean active = true;

    private Integer displayOrder = 0;

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
     * Calculate delivery charge for order amount
     */
    public BigDecimal calculateDeliveryCharge(BigDecimal orderAmount) {
        if (freeDeliveryAbove != null && orderAmount.compareTo(freeDeliveryAbove) >= 0) {
            return BigDecimal.ZERO;
        }
        return deliveryCharge;
    }

    /**
     * Check if area is available for delivery
     */
    public boolean isAvailable() {
        return active && deletedAt == null;
    }

    /**
     * Soft delete
     */
    public void delete() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }
}
