package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateRestaurantRequest(
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @NotNull(message = "Owner ID is required")
    Long ownerId,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    CuisineType cuisineType,

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    String phoneNumber,

    @Email(message = "Invalid email address")
    String email,

    @NotBlank(message = "Address is required")
    String address,

    @NotBlank(message = "City is required")
    String city,

    @NotBlank(message = "State is required")
    String state,

    @NotBlank(message = "ZIP code is required")
    String zipCode,

    String country,

    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    BigDecimal latitude,

    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    BigDecimal longitude,

    @DecimalMin(value = "0.00", message = "Minimum order amount must be positive")
    BigDecimal minimumOrderAmount,

    @DecimalMin(value = "0.00", message = "Delivery fee must be positive")
    BigDecimal deliveryFee,

    @Min(value = 10, message = "Estimated delivery time must be at least 10 minutes")
    @Max(value = 120, message = "Estimated delivery time must not exceed 120 minutes")
    Integer estimatedDeliveryTimeMinutes,

    Boolean acceptsDelivery,

    Boolean acceptsPickup
) {}
