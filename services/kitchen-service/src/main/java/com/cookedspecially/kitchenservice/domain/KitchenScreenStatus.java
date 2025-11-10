package com.cookedspecially.kitchenservice.domain;

/**
 * Status of a kitchen screen
 */
public enum KitchenScreenStatus {
    ACTIVE,    // Screen is active and displaying orders
    INACTIVE,  // Screen is not in use
    OFFLINE,   // Screen is offline/disconnected
    MAINTENANCE // Screen is under maintenance
}
