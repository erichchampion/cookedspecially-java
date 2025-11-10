package com.cookedspecially.loyaltyservice.domain;

/**
 * Type of loyalty/gift card transaction
 */
public enum TransactionType {
    // Loyalty transactions
    EARN,         // Earned points from order
    REDEEM,       // Redeemed points for discount
    EXPIRE,       // Points expired
    ADJUST,       // Manual adjustment by admin

    // Gift card transactions
    PURCHASE,     // Gift card purchased
    ACTIVATE,     // Gift card activated
    USE,          // Gift card used for payment
    REFUND        // Gift card refunded
}
