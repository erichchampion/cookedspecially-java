package com.cookedspecially.loyaltyservice.dto;

import com.cookedspecially.loyaltyservice.domain.Coupon;
import com.cookedspecially.loyaltyservice.domain.CouponStatus;
import com.cookedspecially.loyaltyservice.domain.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CouponResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Integer restaurantId;
    private CouponStatus status;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxTotalUsage;
    private Integer currentUsageCount;
    private Boolean oneTimePerCustomer;
    private String applicableOrderSource;
    private String applicablePaymentMode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;

    public CouponResponse() {}

    public CouponResponse(Coupon coupon) {
        this.id = coupon.getId();
        this.code = coupon.getCode();
        this.name = coupon.getName();
        this.description = coupon.getDescription();
        this.restaurantId = coupon.getRestaurantId();
        this.status = coupon.getStatus();
        this.discountType = coupon.getDiscountType();
        this.discountValue = coupon.getDiscountValue();
        this.minOrderAmount = coupon.getMinOrderAmount();
        this.maxDiscountAmount = coupon.getMaxDiscountAmount();
        this.startDate = coupon.getStartDate();
        this.endDate = coupon.getEndDate();
        this.maxTotalUsage = coupon.getMaxTotalUsage();
        this.currentUsageCount = coupon.getCurrentUsageCount();
        this.oneTimePerCustomer = coupon.getOneTimePerCustomer();
        this.applicableOrderSource = coupon.getApplicableOrderSource();
        this.applicablePaymentMode = coupon.getApplicablePaymentMode();
        this.createdAt = coupon.getCreatedAt();
        this.updatedAt = coupon.getUpdatedAt();
        this.isActive = coupon.isActive();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getCurrentUsageCount() {
        return currentUsageCount;
    }

    public void setCurrentUsageCount(Integer currentUsageCount) {
        this.currentUsageCount = currentUsageCount;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
