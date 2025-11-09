package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create Order Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Order type is required")
    private OrderType orderType;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be positive")
    private BigDecimal subtotal;

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    private BigDecimal taxAmount;

    @NotNull(message = "Delivery charge is required")
    @DecimalMin(value = "0.0", message = "Delivery charge must be non-negative")
    private BigDecimal deliveryCharge;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @Size(max = 1000, message = "Delivery address must not exceed 1000 characters")
    private String deliveryAddress;

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    private String specialInstructions;

    private String couponCode;
}
