package com.cookedspecially.notificationservice.exception;

/**
 * Exception thrown when notification send fails
 */
public class NotificationSendException extends RuntimeException {
    private final String channel;

    public NotificationSendException(String channel, String message) {
        super("Failed to send notification via " + channel + ": " + message);
        this.channel = channel;
    }

    public NotificationSendException(String channel, Throwable cause) {
        super("Failed to send notification via " + channel, cause);
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
