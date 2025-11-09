package com.cookedspecially.orderservice.dto;

import com.cookedspecially.orderservice.domain.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create Order Request DTO
 */
public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId,

    @NotNull(message = "Restaurant ID is required")
    Long restaurantId,

    @NotNull(message = "Order type is required")
    OrderType orderType,

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    List<OrderItemRequest> items,

    @NotNull(message = "Subtotal is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Subtotal must be positive")
    BigDecimal subtotal,

    @NotNull(message = "Tax amount is required")
    @DecimalMin(value = "0.0", message = "Tax amount must be non-negative")
    BigDecimal taxAmount,

    @NotNull(message = "Delivery charge is required")
    @DecimalMin(value = "0.0", message = "Delivery charge must be non-negative")
    BigDecimal deliveryCharge,

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    BigDecimal discountAmount,

    @NotBlank(message = "Payment method is required")
    String paymentMethod,

    @Size(max = 1000, message = "Delivery address must not exceed 1000 characters")
    String deliveryAddress,

    @Size(max = 500, message = "Special instructions must not exceed 500 characters")
    String specialInstructions,

    String couponCode
) {}
