package com.cookedspecially.notificationservice.domain;

/**
 * Notification Status Enum
 */
public enum NotificationStatus {
    PENDING,      // Queued for sending
    PROCESSING,   // Currently being sent
    SENT,         // Successfully sent
    DELIVERED,    // Delivered to recipient
    FAILED,       // Failed to send
    BOUNCED,      // Email bounced
    COMPLAINED    // Marked as spam
}
