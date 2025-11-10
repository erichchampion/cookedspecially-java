package com.cookedspecially.loyaltyservice.domain;

/**
 * Gift card status enum
 */
public enum GiftCardStatus {
    CREATED,      // Created but not activated
    ACTIVE,       // Activated and can be used
    REDEEMED,     // Fully redeemed (balance = 0)
    DEACTIVATED,  // Deactivated by admin
    EXPIRED       // Past expiration date
}
