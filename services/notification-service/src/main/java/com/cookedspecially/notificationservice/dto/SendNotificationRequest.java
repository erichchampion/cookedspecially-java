package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationPriority;
import com.cookedspecially.notificationservice.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * Send Notification Request DTO
 */
public record SendNotificationRequest(
    @NotNull(message = "User ID is required")
    Long userId,

    @NotNull(message = "Notification type is required")
    NotificationType type,

    @NotNull(message = "Channel is required")
    NotificationChannel channel,

    NotificationPriority priority,

    @NotBlank(message = "Subject is required")
    String subject,

    @NotBlank(message = "Content is required")
    String content,

    String recipient,

    String templateId,

    Map<String, Object> templateVariables,

    String relatedEntityType,

    Long relatedEntityId
) {}
