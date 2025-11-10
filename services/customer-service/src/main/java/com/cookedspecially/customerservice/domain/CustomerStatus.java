package com.cookedspecially.customerservice.domain;

/**
 * Customer account status
 */
public enum CustomerStatus {
    ACTIVE,           // Active customer account
    INACTIVE,         // Temporarily inactive
    SUSPENDED,        // Suspended due to policy violation
    PENDING_VERIFICATION,  // Email/phone not verified
    DELETED           // Soft-deleted account
}
