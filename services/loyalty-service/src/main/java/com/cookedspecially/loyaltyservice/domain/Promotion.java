package com.cookedspecially.loyaltyservice.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Promotional campaign entity
 */
@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_start_date", columnList = "startDate"),
    @Index(name = "idx_end_date", columnList = "endDate")
})
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Integer restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PromotionStatus status = PromotionStatus.DRAFT;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    // Associated coupon (if promotion uses a coupon)
    @Column
    private Long couponId;

    @Column(length = 100)
    private String targetAudience; // e.g., "ALL", "NEW_CUSTOMERS", "VIP"

    @Column(length = 100)
    private String channel; // e.g., "EMAIL", "SMS", "PUSH", "ALL"

    @Column(length = 2000)
    private String terms;

    // Statistics
    @Column(nullable = false)
    private Integer impressions = 0;

    @Column(nullable = false)
    private Integer clicks = 0;

    @Column(nullable = false)
    private Integer conversions = 0;

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

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
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

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Integer getImpressions() {
        return impressions;
    }

    public void setImpressions(Integer impressions) {
        this.impressions = impressions;
    }

    public Integer getClicks() {
        return clicks;
    }

    public void setClicks(Integer clicks) {
        this.clicks = clicks;
    }

    public Integer getConversions() {
        return conversions;
    }

    public void setConversions(Integer conversions) {
        this.conversions = conversions;
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
        if (status != PromotionStatus.ACTIVE) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }
        if (endDate != null && now.isAfter(endDate)) {
            return false;
        }
        return true;
    }

    public void incrementImpressions() {
        this.impressions++;
    }

    public void incrementClicks() {
        this.clicks++;
    }

    public void incrementConversions() {
        this.conversions++;
    }

    public double getConversionRate() {
        if (clicks == 0) {
            return 0.0;
        }
        return (conversions * 100.0) / clicks;
    }
}
