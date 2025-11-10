package com.cookedspecially.kitchenservice.domain;

/**
 * Operating status of a till/cash register
 */
public enum TillStatus {
    OPEN,      // Till is open and accepting transactions
    CLOSED,    // Till is closed for the shift
    SUSPENDED, // Temporarily suspended
    INACTIVE   // Not in use
}
