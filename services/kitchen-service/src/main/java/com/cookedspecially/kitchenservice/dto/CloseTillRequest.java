package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for closing a till
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CloseTillRequest {

    @NotNull(message = "Closing balance is required")
    @PositiveOrZero(message = "Closing balance must be zero or positive")
    private BigDecimal closingBalance;
}
