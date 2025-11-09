package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.PaymentProvider;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Create Payment Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "100000.00", message = "Amount cannot exceed 100,000")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (ISO 4217)")
    private String currency;

    @NotNull(message = "Payment provider is required")
    private PaymentProvider provider;

    private Long paymentMethodId;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private String metadata;
}
