package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Order Status Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
}
