package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for opening a till
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenTillRequest {

    @NotNull(message = "Opening balance is required")
    @PositiveOrZero(message = "Opening balance must be zero or positive")
    private BigDecimal openingBalance;

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "User name is required")
    private String userName;
}
