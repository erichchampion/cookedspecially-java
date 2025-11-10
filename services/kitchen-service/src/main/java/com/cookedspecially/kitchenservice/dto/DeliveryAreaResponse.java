package com.cookedspecially.kitchenservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for delivery area information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAreaResponse {
    private Long id;
    private String name;
    private String description;
    private Long restaurantId;
    private BigDecimal deliveryCharge;
    private BigDecimal minimumOrderAmount;
    private BigDecimal freeDeliveryAbove;
    private Integer estimatedDeliveryTime;
    private String zipCode;
    private String city;
    private String state;
    private String country;
    private Boolean active;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
