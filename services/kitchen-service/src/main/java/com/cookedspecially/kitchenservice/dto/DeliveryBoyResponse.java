package com.cookedspecially.kitchenservice.dto;

import com.cookedspecially.kitchenservice.domain.DeliveryBoyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for delivery boy information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryBoyResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private Long restaurantId;
    private DeliveryBoyStatus status;
    private String vehicleType;
    private String vehicleNumber;
    private String licenseNumber;
    private Integer currentDeliveryCount;
    private Integer totalDeliveriesCompleted;
    private Double averageRating;
    private Integer ratingCount;
    private String notes;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
