package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a delivery area
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeliveryAreaRequest {

    @NotBlank(message = "Area name is required")
    private String name;

    private String description;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Delivery charge is required")
    @PositiveOrZero(message = "Delivery charge must be zero or positive")
    private BigDecimal deliveryCharge;

    @PositiveOrZero(message = "Minimum order amount must be zero or positive")
    private BigDecimal minimumOrderAmount;

    @PositiveOrZero(message = "Free delivery threshold must be zero or positive")
    private BigDecimal freeDeliveryAbove;

    private Integer estimatedDeliveryTime;

    private String zipCode;

    private String city;

    private String state;

    private String country;

    private Integer displayOrder = 0;
}
