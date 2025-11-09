package com.cookedspecially.restaurantservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create Menu Item Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMenuItemRequest {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    @Size(max = 50)
    private String category;

    @Size(max = 500)
    private String imageUrl;

    @Builder.Default
    private Boolean isAvailable = true;

    @Builder.Default
    private Boolean isVegetarian = false;

    @Builder.Default
    private Boolean isVegan = false;

    @Builder.Default
    private Boolean isGlutenFree = false;

    @Min(value = 0, message = "Calories must be non-negative")
    private Integer calories;

    @Min(value = 0, message = "Preparation time must be non-negative")
    private Integer preparationTimeMinutes;

    private Integer spiceLevel;
}
