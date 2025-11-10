package com.cookedspecially.loyaltyservice.dto;

import com.cookedspecially.loyaltyservice.domain.CouponStatus;
import com.cookedspecially.loyaltyservice.domain.DiscountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Coupon name is required")
    @Size(max = 200, message = "Coupon name must not exceed 200 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotNull(message = "Restaurant ID is required")
    private Integer restaurantId;

    private CouponStatus status = CouponStatus.DISABLED;

    @NotNull(message = "Discount type is required")
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Discount value must be greater than 0")
    private BigDecimal discountValue;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be 0 or greater")
    private BigDecimal minOrderAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum discount amount must be greater than 0")
    private BigDecimal maxDiscountAmount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Min(value = 1, message = "Max total usage must be at least 1")
    private Integer maxTotalUsage;

    private Boolean oneTimePerCustomer = false;
    private String applicableOrderSource;
    private String applicablePaymentMode;

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Integer restaurantId) {
        this.restaurantId = restaurantId;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public void setStatus(CouponStatus status) {
        this.status = status;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public BigDecimal getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(BigDecimal minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public BigDecimal getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(BigDecimal maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getMaxTotalUsage() {
        return maxTotalUsage;
    }

    public void setMaxTotalUsage(Integer maxTotalUsage) {
        this.maxTotalUsage = maxTotalUsage;
    }

    public Boolean getOneTimePerCustomer() {
        return oneTimePerCustomer;
    }

    public void setOneTimePerCustomer(Boolean oneTimePerCustomer) {
        this.oneTimePerCustomer = oneTimePerCustomer;
    }

    public String getApplicableOrderSource() {
        return applicableOrderSource;
    }

    public void setApplicableOrderSource(String applicableOrderSource) {
        this.applicableOrderSource = applicableOrderSource;
    }

    public String getApplicablePaymentMode() {
        return applicablePaymentMode;
    }

    public void setApplicablePaymentMode(String applicablePaymentMode) {
        this.applicablePaymentMode = applicablePaymentMode;
    }
}
