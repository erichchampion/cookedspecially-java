package com.cookedspecially.notificationservice.domain;

/**
 * Notification Type Enum
 */
public enum NotificationType {
    // Order notifications
    ORDER_PLACED,
    ORDER_CONFIRMED,
    ORDER_PREPARING,
    ORDER_OUT_FOR_DELIVERY,
    ORDER_DELIVERED,
    ORDER_CANCELLED,

    // Payment notifications
    PAYMENT_RECEIVED,
    PAYMENT_FAILED,
    REFUND_PROCESSED,

    // Restaurant notifications
    RESTAURANT_APPROVED,
    RESTAURANT_SUSPENDED,
    RESTAURANT_REOPENED,
    MENU_UPDATED,
    NEW_REVIEW,

    // General notifications
    WELCOME,
    PASSWORD_RESET,
    ACCOUNT_VERIFIED,
    PROMOTIONAL,
    SYSTEM_ALERT
}
