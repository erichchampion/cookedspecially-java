package com.cookedspecially.loyaltyservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ValidateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Order amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Order amount must be greater than 0")
    private BigDecimal orderAmount;

    private String orderSource;
    private String paymentMode;

    // Getters and Setters
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
