package com.cookedspecially.loyaltyservice.domain;

/**
 * Coupon status enum
 */
public enum CouponStatus {
    ENABLED,      // Active and can be used
    DISABLED,     // Temporarily disabled
    EXPIRED,      // Past expiration date
    DEPLETED      // Max usage count reached
}
