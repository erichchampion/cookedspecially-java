package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.NotificationPreference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Notification Preference Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceResponse {

    private Long id;
    private Long userId;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean inAppEnabled;
    private Boolean orderNotifications;
    private Boolean paymentNotifications;
    private Boolean restaurantNotifications;
    private Boolean promotionalNotifications;
    private Boolean quietHoursEnabled;
    private String quietHoursStart;
    private String quietHoursEnd;
    private String emailAddress;
    private String phoneNumber;
    private String locale;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert entity to DTO (excluding sensitive device tokens)
     */
    public static NotificationPreferenceResponse fromEntity(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
            .id(preference.getId())
            .userId(preference.getUserId())
            .emailEnabled(preference.getEmailEnabled())
            .smsEnabled(preference.getSmsEnabled())
            .pushEnabled(preference.getPushEnabled())
            .inAppEnabled(preference.getInAppEnabled())
            .orderNotifications(preference.getOrderNotifications())
            .paymentNotifications(preference.getPaymentNotifications())
            .restaurantNotifications(preference.getRestaurantNotifications())
            .promotionalNotifications(preference.getPromotionalNotifications())
            .quietHoursEnabled(preference.getQuietHoursEnabled())
            .quietHoursStart(preference.getQuietHoursStart())
            .quietHoursEnd(preference.getQuietHoursEnd())
            .emailAddress(preference.getEmailAddress())
            .phoneNumber(preference.getPhoneNumber())
            .locale(preference.getLocale())
            .createdAt(preference.getCreatedAt())
            .updatedAt(preference.getUpdatedAt())
            .build();
    }
}
