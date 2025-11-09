package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRestaurantRequest {

    @NotBlank(message = "Restaurant name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private CuisineType cuisineType;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;

    @Email(message = "Invalid email address")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "ZIP code is required")
    private String zipCode;

    @Builder.Default
    private String country = "USA";

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal longitude;

    @DecimalMin(value = "0.00", message = "Minimum order amount must be positive")
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.00", message = "Delivery fee must be positive")
    private BigDecimal deliveryFee;

    @Min(value = 10, message = "Estimated delivery time must be at least 10 minutes")
    @Max(value = 120, message = "Estimated delivery time must not exceed 120 minutes")
    private Integer estimatedDeliveryTimeMinutes;

    @Builder.Default
    private Boolean acceptsDelivery = true;

    @Builder.Default
    private Boolean acceptsPickup = true;
}
