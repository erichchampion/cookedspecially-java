package com.cookedspecially.notificationservice.exception;

/**
 * Exception thrown when notification is not found
 */
public class NotificationNotFoundException extends RuntimeException {
    private final Long notificationId;

    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with ID: " + notificationId);
        this.notificationId = notificationId;
    }

    public Long getNotificationId() {
        return notificationId;
    }
}
