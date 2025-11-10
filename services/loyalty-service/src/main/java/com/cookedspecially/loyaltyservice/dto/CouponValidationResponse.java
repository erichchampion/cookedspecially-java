package com.cookedspecially.loyaltyservice.dto;

import java.math.BigDecimal;

public class CouponValidationResponse {
    private Boolean valid;
    private String message;
    private BigDecimal discountAmount;
    private CouponResponse coupon;

    public CouponValidationResponse() {}

    public CouponValidationResponse(Boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public CouponValidationResponse(Boolean valid, String message, BigDecimal discountAmount, CouponResponse coupon) {
        this.valid = valid;
        this.message = message;
        this.discountAmount = discountAmount;
        this.coupon = coupon;
    }

    // Getters and Setters
    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public CouponResponse getCoupon() {
        return coupon;
    }

    public void setCoupon(CouponResponse coupon) {
        this.coupon = coupon;
    }
}
