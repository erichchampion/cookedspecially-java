package com.cookedspecially.notificationservice.domain;

/**
 * Notification Priority Enum
 */
public enum NotificationPriority {
    LOW,      // Can be batched or delayed
    MEDIUM,   // Send when convenient
    HIGH,     // Send immediately
    URGENT    // Critical, must be sent immediately
}
