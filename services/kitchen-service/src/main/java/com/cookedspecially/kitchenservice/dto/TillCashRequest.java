package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for adding/withdrawing cash from till
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillCashRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String notes;

    @NotNull(message = "Performed by user ID is required")
    private String performedBy;

    @NotNull(message = "Performed by user name is required")
    private String performedByName;
}
