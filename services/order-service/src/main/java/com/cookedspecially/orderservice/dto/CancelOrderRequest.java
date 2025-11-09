package com.cookedspecially.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Cancel Order Request DTO
 */
public record CancelOrderRequest(
    @NotBlank(message = "Cancellation reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    String reason
) {}
