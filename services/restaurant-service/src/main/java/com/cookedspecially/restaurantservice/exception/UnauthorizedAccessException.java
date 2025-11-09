package com.cookedspecially.restaurantservice.exception;

/**
 * Exception thrown when user attempts unauthorized access
 */
public class UnauthorizedAccessException extends RuntimeException {
    private final Long userId;
    private final Long resourceId;

    public UnauthorizedAccessException(Long userId, Long resourceId) {
        super(String.format("User %d is not authorized to access resource %d", userId, resourceId));
        this.userId = userId;
        this.resourceId = resourceId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getResourceId() {
        return resourceId;
    }
}
