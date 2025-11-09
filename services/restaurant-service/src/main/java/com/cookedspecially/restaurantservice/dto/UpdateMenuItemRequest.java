package com.cookedspecially.restaurantservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Update Menu Item Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMenuItemRequest {

    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @Size(max = 50)
    private String category;

    @Size(max = 500)
    private String imageUrl;

    private Boolean isAvailable;

    private Boolean isVegetarian;

    private Boolean isVegan;

    private Boolean isGlutenFree;

    @Min(value = 0, message = "Calories must be non-negative")
    private Integer calories;

    @Min(value = 0, message = "Preparation time must be non-negative")
    private Integer preparationTimeMinutes;

    private Integer spiceLevel;
}
