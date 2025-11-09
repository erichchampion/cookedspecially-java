package com.cookedspecially.restaurantservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Update Menu Item Request DTO
 */
public record UpdateMenuItemRequest(
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    BigDecimal price,

    @Size(max = 50)
    String category,

    @Size(max = 500)
    String imageUrl,

    Boolean isAvailable,

    Boolean isVegetarian,

    Boolean isVegan,

    Boolean isGlutenFree,

    @Min(value = 0, message = "Calories must be non-negative")
    Integer calories,

    @Min(value = 0, message = "Preparation time must be non-negative")
    Integer preparationTimeMinutes,

    Integer spiceLevel
) {}
