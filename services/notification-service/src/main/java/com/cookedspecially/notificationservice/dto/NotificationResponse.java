package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.*;

import java.time.LocalDateTime;

/**
 * Notification Response DTO
 */
public class NotificationResponse {

    private Long id;
    private Long userId;
    private NotificationType type;
    private NotificationChannel channel;
    private NotificationStatus status;
    private NotificationPriority priority;
    private String subject;
    private String content;
    private String recipient;
    private String relatedEntityType;
    private Long relatedEntityId;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private String errorMessage;
    private Integer retryCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO
     */

    // Constructors
    public NotificationResponse() {
    }

    public NotificationResponse(Long id,
                 Long userId,
                 NotificationType type,
                 NotificationChannel channel,
                 NotificationStatus status,
                 NotificationPriority priority,
                 String subject,
                 String content,
                 String recipient,
                 String relatedEntityType,
                 Long relatedEntityId,
                 LocalDateTime sentAt,
                 LocalDateTime deliveredAt,
                 String errorMessage,
                 Integer retryCount,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.channel = channel;
        this.status = status;
        this.priority = priority;
        this.subject = subject;
        this.content = content;
        this.recipient = recipient;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public NotificationType getType() {
        return type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static NotificationResponse fromEntity(Notification notification) {
        return new NotificationResponse(
            notification.getId(),
            notification.getUserId(),
            notification.getType(),
            notification.getChannel(),
            notification.getStatus(),
            notification.getPriority(),
            notification.getSubject(),
            notification.getContent(),
            notification.getRecipient(),
            notification.getRelatedEntityType(),
            notification.getRelatedEntityId(),
            notification.getSentAt(),
            notification.getDeliveredAt(),
            notification.getErrorMessage(),
            notification.getRetryCount(),
            notification.getCreatedAt(),
            notification.getUpdatedAt()
        );
    }
}
