package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for occupying a table
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupyTableRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;
}
