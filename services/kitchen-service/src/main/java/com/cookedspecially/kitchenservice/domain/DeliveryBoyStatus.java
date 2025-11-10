package com.cookedspecially.kitchenservice.domain;

/**
 * Status of delivery personnel
 */
public enum DeliveryBoyStatus {
    AVAILABLE,     // Available for delivery assignment
    ON_DELIVERY,   // Currently on a delivery
    BREAK,         // On break
    OFFLINE,       // Not available/logged out
    INACTIVE       // Not actively working
}
