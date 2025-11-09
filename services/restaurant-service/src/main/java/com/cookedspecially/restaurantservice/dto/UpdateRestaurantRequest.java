package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Update Restaurant Request DTO
 */
public record UpdateRestaurantRequest(
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    RestaurantStatus status,

    CuisineType cuisineType,

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Invalid phone number format")
    String phoneNumber,

    @Email(message = "Invalid email format")
    @Size(max = 200)
    String email,

    @Size(max = 500, message = "Address must not exceed 500 characters")
    String address,

    @Size(max = 100)
    String city,

    @Size(max = 50)
    String state,

    @Size(max = 20)
    String zipCode,

    @Size(max = 50)
    String country,

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    BigDecimal latitude,

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    BigDecimal longitude,

    @Size(max = 500)
    String imageUrl,

    @Size(max = 500)
    String logoUrl,

    @DecimalMin(value = "0.00", message = "Minimum order amount must be non-negative")
    BigDecimal minimumOrderAmount,

    @DecimalMin(value = "0.00", message = "Delivery fee must be non-negative")
    BigDecimal deliveryFee,

    @Min(value = 0, message = "Estimated delivery time must be non-negative")
    Integer estimatedDeliveryTimeMinutes,

    Boolean isActive,

    Boolean acceptsDelivery,

    Boolean acceptsPickup
) {}
