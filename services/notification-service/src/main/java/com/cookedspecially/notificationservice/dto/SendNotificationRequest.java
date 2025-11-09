package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.NotificationChannel;
import com.cookedspecially.notificationservice.domain.NotificationPriority;
import com.cookedspecially.notificationservice.domain.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Send Notification Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Channel is required")
    private NotificationChannel channel;

    @Builder.Default
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private String recipient;  // Optional override for email/phone/device token

    // Template-based notification
    private String templateId;
    private Map<String, Object> templateVariables;

    // Related entity tracking
    private String relatedEntityType;
    private Long relatedEntityId;
}
