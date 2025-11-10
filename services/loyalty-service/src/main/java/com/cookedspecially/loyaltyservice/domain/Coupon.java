package com.cookedspecially.loyaltyservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Coupon entity for discount coupons
 */
@Entity
@Table(name = "coupons", indexes = {
    @Index(name = "idx_coupon_code", columnList = "code"),
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_status", columnList = "status")
})
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponStatus status = CouponStatus.DISABLED;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    // For PERCENTAGE: value between 0-100, For FIXED_AMOUNT: actual amount
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    // Minimum order amount required to apply coupon
    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    // Maximum discount amount (useful for percentage discounts)
    @Column(precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    // Usage limits
    @Column
    private Integer maxTotalUsage; // null = unlimited

    @Column(nullable = false)
    private Integer currentUsageCount = 0;

    @Column(nullable = false)
    private Boolean oneTimePerCustomer = false;

    // Restrictions
    @Column(length = 50)
    private String applicableOrderSource; // e.g., "ONLINE", "MOBILE", "ANY"

    @Column(length = 50)
    private String applicablePaymentMode; // e.g., "CARD", "CASH", "ANY"

    // Audit fields
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Integer createdBy; // user ID

    @Column
    private Integer updatedBy; // user ID

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    // Business methods
    public boolean isActive() {
        if (status != CouponStatus.ENABLED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        if (maxTotalUsage != null && currentUsageCount >= maxTotalUsage) {
            return false;
        }
        return true;
    }

    public void incrementUsage() {
        this.currentUsageCount++;
        if (maxTotalUsage != null && currentUsageCount >= maxTotalUsage) {
            this.status = CouponStatus.DEPLETED;
        }
    }

    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (discountType == DiscountType.PERCENTAGE) {
            BigDecimal discount = orderAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                return maxDiscountAmount;
            }
            return discount;
        } else if (discountType == DiscountType.FIXED_AMOUNT) {
            return discountValue;
        }
        return BigDecimal.ZERO;
    }
}
