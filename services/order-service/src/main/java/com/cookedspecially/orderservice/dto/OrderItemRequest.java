package com.cookedspecially.orderservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Order Item Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    @NotBlank(message = "Menu item name is required")
    @Size(max = 255, message = "Menu item name must not exceed 255 characters")
    private String menuItemName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 50, message = "Quantity cannot exceed 50")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    private String specialInstructions;
}
