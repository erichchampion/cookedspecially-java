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
 * Kitchen Screen entity
 * Represents a kitchen display screen for order management
 */
@Entity
@Table(name = "kitchen_screens", indexes = {
    @Index(name = "idx_fulfillment_center", columnList = "fulfillmentCenterId"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenScreen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Screen name is required")
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
    private KitchenScreenStatus status = KitchenScreenStatus.ACTIVE;

    @Column(length = 50)
    private String stationType; // Grill, Fry, Salad, Dessert, etc.

    @Column(length = 100)
    private String ipAddress; // Screen device IP for WebSocket connection

    @Column(length = 200)
    private String deviceId; // Unique device identifier

    private Boolean soundEnabled = true; // Enable sound alerts for new orders

    private Boolean autoAcceptOrders = false; // Auto-accept orders without confirmation

    private Integer displayOrder = 0; // Order for display in UI

    private LocalDateTime lastHeartbeat; // Last time screen sent heartbeat

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
     * Update heartbeat timestamp
     */
    public void updateHeartbeat() {
        this.lastHeartbeat = LocalDateTime.now();
        if (this.status == KitchenScreenStatus.OFFLINE) {
            this.status = KitchenScreenStatus.ACTIVE;
        }
    }

    /**
     * Check if screen is online (heartbeat within last 2 minutes)
     */
    public boolean isOnline() {
        if (lastHeartbeat == null) {
            return false;
        }
        return lastHeartbeat.isAfter(LocalDateTime.now().minusMinutes(2));
    }

    /**
     * Mark screen as offline
     */
    public void markOffline() {
        this.status = KitchenScreenStatus.OFFLINE;
    }
}
