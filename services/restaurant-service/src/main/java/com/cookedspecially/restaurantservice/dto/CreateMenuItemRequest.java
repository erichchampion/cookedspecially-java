package com.cookedspecially.restaurantservice.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Create Menu Item Request DTO
 */
public record CreateMenuItemRequest(
    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    String name,

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    String description,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    BigDecimal price,

    @NotBlank(message = "Category is required")
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
