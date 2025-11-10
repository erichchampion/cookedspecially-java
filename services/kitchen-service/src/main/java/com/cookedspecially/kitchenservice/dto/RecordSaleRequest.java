package com.cookedspecially.kitchenservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for recording a sale transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordSaleRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private String paymentMethod;

    @NotNull(message = "Performed by user ID is required")
    private String performedBy;

    @NotNull(message = "Performed by user name is required")
    private String performedByName;
}
