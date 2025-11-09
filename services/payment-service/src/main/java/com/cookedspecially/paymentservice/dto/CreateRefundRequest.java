package com.cookedspecially.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create Refund Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRefundRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Reason is required")
    @Size(max = 500, message = "Reason must not exceed 500 characters")
    private String reason;
}
