package com.cookedspecially.loyaltyservice.domain;

/**
 * Promotion campaign status
 */
public enum PromotionStatus {
    DRAFT,        // Being created
    SCHEDULED,    // Scheduled for future
    ACTIVE,       // Currently running
    PAUSED,       // Temporarily paused
    COMPLETED,    // Ended successfully
    CANCELLED     // Cancelled before completion
}
