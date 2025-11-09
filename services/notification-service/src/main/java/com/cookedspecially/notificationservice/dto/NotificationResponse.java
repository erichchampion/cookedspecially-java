package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Notification Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    public static NotificationResponse fromEntity(Notification notification) {
        return NotificationResponse.builder()
            .id(notification.getId())
            .userId(notification.getUserId())
            .type(notification.getType())
            .channel(notification.getChannel())
            .status(notification.getStatus())
            .priority(notification.getPriority())
            .subject(notification.getSubject())
            .content(notification.getContent())
            .recipient(notification.getRecipient())
            .relatedEntityType(notification.getRelatedEntityType())
            .relatedEntityId(notification.getRelatedEntityId())
            .sentAt(notification.getSentAt())
            .deliveredAt(notification.getDeliveredAt())
            .errorMessage(notification.getErrorMessage())
            .retryCount(notification.getRetryCount())
            .createdAt(notification.getCreatedAt())
            .updatedAt(notification.getUpdatedAt())
            .build();
    }
}
