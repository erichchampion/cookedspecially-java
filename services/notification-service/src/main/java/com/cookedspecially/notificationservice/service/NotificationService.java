package com.cookedspecially.notificationservice.service;

import com.cookedspecially.notificationservice.domain.*;
import com.cookedspecially.notificationservice.dto.NotificationResponse;
import com.cookedspecially.notificationservice.dto.SendNotificationRequest;
import com.cookedspecially.notificationservice.exception.NotificationNotFoundException;
import com.cookedspecially.notificationservice.exception.NotificationSendException;
import com.cookedspecially.notificationservice.exception.RateLimitExceededException;
import com.cookedspecially.notificationservice.repository.NotificationPreferenceRepository;
import com.cookedspecially.notificationservice.repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

/**
 * Notification Service - Orchestrates notification sending across all channels
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    private final PushNotificationService pushNotificationService;
    private final TemplateService templateService;
    private final ObjectMapper objectMapper;

    @Value("${notification.rate-limit.email-per-user-per-hour:10}")
    private int emailRateLimit;

    @Value("${notification.rate-limit.sms-per-user-per-hour:5}")
    private int smsRateLimit;

    @Value("${notification.rate-limit.push-per-user-per-hour:20}")
    private int pushRateLimit;

    /**
     * Send notification
     */
    @Transactional
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending {} notification to user {} via {}",
            request.getType(), request.getUserId(), request.getChannel());

        // Get user preferences
        NotificationPreference preferences = getOrCreatePreferences(request.getUserId());

        // Check if user wants this type of notification
        if (!preferences.wantsNotificationType(request.getType())) {
            log.info("User {} has disabled {} notifications",
                request.getUserId(), request.getType());
            return null;
        }

        // Check if user can receive on this channel
        if (!preferences.canReceiveOnChannel(request.getChannel())) {
            log.info("User {} cannot receive notifications on {}",
                request.getUserId(), request.getChannel());
            return null;
        }

        // Check quiet hours
        if (isInQuietHours(preferences)) {
            log.info("User {} is in quiet hours, deferring notification",
                request.getUserId());
            // Could queue for later delivery
            return null;
        }

        // Check rate limits
        checkRateLimit(request.getUserId(), request.getChannel());

        // Determine recipient
        String recipient = determineRecipient(request, preferences);
        if (recipient == null) {
            log.warn("No recipient available for user {} on channel {}",
                request.getUserId(), request.getChannel());
            return null;
        }

        // Create notification record
        Notification notification = createNotificationRecord(request, recipient, preferences);

        // Send asynchronously
        sendNotificationAsync(notification);

        return NotificationResponse.fromEntity(notification);
    }

    /**
     * Send notification asynchronously
     */
    @Async
    public void sendNotificationAsync(Notification notification) {
        try {
            String externalMessageId = null;
            String provider = null;

            switch (notification.getChannel()) {
                case EMAIL -> {
                    externalMessageId = sendEmail(notification);
                    provider = "SES";
                }
                case SMS -> {
                    externalMessageId = sendSms(notification);
                    provider = "SNS_SMS";
                }
                case PUSH -> {
                    externalMessageId = sendPush(notification);
                    provider = "SNS_PUSH";
                }
                case IN_APP -> {
                    // In-app notifications are just stored in DB
                    externalMessageId = "IN_APP_" + notification.getId();
                    provider = "IN_APP";
                }
            }

            notification.markAsSent(externalMessageId, provider);
            notificationRepository.save(notification);

            log.info("Notification {} sent successfully via {}",
                notification.getId(), notification.getChannel());

        } catch (Exception e) {
            log.error("Failed to send notification {}: {}",
                notification.getId(), e.getMessage(), e);

            notification.markAsFailed(e.getMessage());
            notificationRepository.save(notification);

            // Retry if possible
            if (notification.canRetry()) {
                retryNotification(notification);
            }
        }
    }

    /**
     * Send email notification
     */
    private String sendEmail(Notification notification) {
        String subject = notification.getSubject();
        String content = notification.getContent();

        // Process template if specified
        if (notification.getTemplateId() != null) {
            Map<String, Object> variables = parseTemplateVariables(notification.getTemplateVariables());
            subject = templateService.processSubject(notification.getTemplateId(), variables);
            content = templateService.processTemplate(notification.getTemplateId(), variables);
        }

        return emailService.sendHtmlEmail(
            notification.getRecipient(),
            subject,
            content,
            stripHtml(content)
        );
    }

    /**
     * Send SMS notification
     */
    private String sendSms(Notification notification) {
        String message = notification.getContent();

        // Process template if specified
        if (notification.getTemplateId() != null) {
            Map<String, Object> variables = parseTemplateVariables(notification.getTemplateVariables());
            message = templateService.processTemplate(notification.getTemplateId(), variables);
        }

        // Truncate SMS to 160 characters
        if (message.length() > 160) {
            message = message.substring(0, 157) + "...";
        }

        return smsService.sendSms(notification.getRecipient(), message);
    }

    /**
     * Send push notification
     */
    private String sendPush(Notification notification) {
        String title = notification.getSubject();
        String body = notification.getContent();

        // Process template if specified
        Map<String, Object> data = null;
        if (notification.getTemplateId() != null) {
            Map<String, Object> variables = parseTemplateVariables(notification.getTemplateVariables());
            title = templateService.processSubject(notification.getTemplateId(), variables);
            body = templateService.processTemplate(notification.getTemplateId(), variables);
            data = variables;
        }

        // Determine platform based on device token format
        String deviceToken = notification.getRecipient();
        if (deviceToken.length() == 64) {
            // iOS device token (hex string)
            return pushNotificationService.sendIosPush(deviceToken, title, body, (Map<String, String>) data);
        } else {
            // Android device token (FCM token)
            return pushNotificationService.sendAndroidPush(deviceToken, title, body, (Map<String, String>) data);
        }
    }

    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        return NotificationResponse.fromEntity(notification);
    }

    /**
     * Get user notifications
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(NotificationResponse::fromEntity);
    }

    /**
     * Get user notifications by status
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getUserNotificationsByStatus(
        Long userId, NotificationStatus status, Pageable pageable) {

        Page<Notification> notifications = notificationRepository
            .findByUserIdAndStatus(userId, status, pageable);

        return notifications.map(NotificationResponse::fromEntity);
    }

    /**
     * Mark notification as read (for in-app)
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        notification.markAsDelivered();
        notificationRepository.save(notification);
    }

    /**
     * Retry failed notification
     */
    @Async
    @Transactional
    public void retryNotification(Notification notification) {
        if (!notification.canRetry()) {
            log.warn("Notification {} has exceeded max retries", notification.getId());
            return;
        }

        log.info("Retrying notification {} (attempt {})",
            notification.getId(), notification.getRetryCount() + 1);

        // Exponential backoff
        int delay = (int) (1000 * Math.pow(2, notification.getRetryCount()));
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        notification.setStatus(NotificationStatus.PENDING);
        notificationRepository.save(notification);

        sendNotificationAsync(notification);
    }

    /**
     * Check rate limit for user and channel
     */
    private void checkRateLimit(Long userId, NotificationChannel channel) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        long count = notificationRepository.countByUserIdAndChannelSince(
            userId, channel, oneHourAgo);

        int limit = switch (channel) {
            case EMAIL -> emailRateLimit;
            case SMS -> smsRateLimit;
            case PUSH -> pushRateLimit;
            case IN_APP -> 100; // Higher limit for in-app
        };

        if (count >= limit) {
            throw new RateLimitExceededException(userId, channel.name());
        }
    }

    /**
     * Check if current time is in quiet hours
     */
    private boolean isInQuietHours(NotificationPreference preferences) {
        if (!preferences.getQuietHoursEnabled()) {
            return false;
        }

        if (preferences.getQuietHoursStart() == null || preferences.getQuietHoursEnd() == null) {
            return false;
        }

        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(preferences.getQuietHoursStart());
        LocalTime end = LocalTime.parse(preferences.getQuietHoursEnd());

        if (start.isBefore(end)) {
            return !now.isBefore(start) && now.isBefore(end);
        } else {
            // Quiet hours span midnight
            return !now.isBefore(start) || now.isBefore(end);
        }
    }

    /**
     * Determine recipient based on channel and preferences
     */
    private String determineRecipient(SendNotificationRequest request, NotificationPreference preferences) {
        if (request.getRecipient() != null) {
            return request.getRecipient();
        }

        return switch (request.getChannel()) {
            case EMAIL -> preferences.getEmailAddress();
            case SMS -> preferences.getPhoneNumber();
            case PUSH -> preferences.getAndroidDeviceToken() != null ?
                preferences.getAndroidDeviceToken() : preferences.getIosDeviceToken();
            case IN_APP -> String.valueOf(request.getUserId());
        };
    }

    /**
     * Create notification record
     */
    private Notification createNotificationRecord(
        SendNotificationRequest request, String recipient, NotificationPreference preferences) {

        String templateVars = null;
        if (request.getTemplateVariables() != null) {
            try {
                templateVars = objectMapper.writeValueAsString(request.getTemplateVariables());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize template variables", e);
            }
        }

        Notification notification = Notification.builder()
            .userId(request.getUserId())
            .type(request.getType())
            .channel(request.getChannel())
            .status(NotificationStatus.PENDING)
            .priority(request.getPriority())
            .subject(request.getSubject())
            .content(request.getContent())
            .recipient(recipient)
            .templateId(request.getTemplateId())
            .templateVariables(templateVars)
            .relatedEntityType(request.getRelatedEntityType())
            .relatedEntityId(request.getRelatedEntityId())
            .retryCount(0)
            .build();

        return notificationRepository.save(notification);
    }

    /**
     * Get or create user preferences
     */
    private NotificationPreference getOrCreatePreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
            .orElseGet(() -> {
                NotificationPreference pref = NotificationPreference.builder()
                    .userId(userId)
                    .build();
                return preferenceRepository.save(pref);
            });
    }

    /**
     * Parse template variables JSON
     */
    private Map<String, Object> parseTemplateVariables(String json) {
        if (json == null || json.isEmpty()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse template variables", e);
            return Map.of();
        }
    }

    /**
     * Strip HTML tags from content
     */
    private String stripHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<[^>]*>", "");
    }
}
