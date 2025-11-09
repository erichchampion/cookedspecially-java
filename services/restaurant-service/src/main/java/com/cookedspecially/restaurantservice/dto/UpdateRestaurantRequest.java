package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Update Restaurant Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRestaurantRequest {

    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private RestaurantStatus status;

    private CuisineType cuisineType;

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Invalid phone number format")
    private String phoneNumber;

    @Email(message = "Invalid email format")
    @Size(max = 200)
    private String email;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 100)
    private String city;

    @Size(max = 50)
    private String state;

    @Size(max = 20)
    private String zipCode;

    @Size(max = 50)
    private String country;

    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal longitude;

    @Size(max = 500)
    private String imageUrl;

    @Size(max = 500)
    private String logoUrl;

    @DecimalMin(value = "0.00", message = "Minimum order amount must be non-negative")
    private BigDecimal minimumOrderAmount;

    @DecimalMin(value = "0.00", message = "Delivery fee must be non-negative")
    private BigDecimal deliveryFee;

    @Min(value = 0, message = "Estimated delivery time must be non-negative")
    private Integer estimatedDeliveryTimeMinutes;

    private Boolean isActive;

    private Boolean acceptsDelivery;

    private Boolean acceptsPickup;
}
