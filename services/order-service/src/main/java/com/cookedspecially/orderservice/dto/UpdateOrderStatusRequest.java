package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Update Order Status Request DTO
 */
public record UpdateOrderStatusRequest(
    @NotNull(message = "Status is required")
    OrderStatus status
) {}
