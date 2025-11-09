package com.cookedspecially.notificationservice.exception;

/**
 * Exception thrown when rate limit is exceeded
 */
public class RateLimitExceededException extends RuntimeException {
    private final Long userId;
    private final String channel;

    public RateLimitExceededException(Long userId, String channel) {
        super(String.format("Rate limit exceeded for user %d on channel %s", userId, channel));
        this.userId = userId;
        this.channel = channel;
    }

    public Long getUserId() {
        return userId;
    }

    public String getChannel() {
        return channel;
    }
}
