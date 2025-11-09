package com.cookedspecially.notificationservice.dto;

import com.cookedspecially.notificationservice.domain.NotificationPreference;

import java.time.LocalDateTime;

/**
 * Notification Preference Response DTO
 */
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

    // Constructors
    public NotificationPreferenceResponse() {
    }

    public NotificationPreferenceResponse(Long id,
                 Long userId,
                 Boolean emailEnabled,
                 Boolean smsEnabled,
                 Boolean pushEnabled,
                 Boolean inAppEnabled,
                 Boolean orderNotifications,
                 Boolean paymentNotifications,
                 Boolean restaurantNotifications,
                 Boolean promotionalNotifications,
                 Boolean quietHoursEnabled,
                 String quietHoursStart,
                 String quietHoursEnd,
                 String emailAddress,
                 String phoneNumber,
                 String locale,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.emailEnabled = emailEnabled;
        this.smsEnabled = smsEnabled;
        this.pushEnabled = pushEnabled;
        this.inAppEnabled = inAppEnabled;
        this.orderNotifications = orderNotifications;
        this.paymentNotifications = paymentNotifications;
        this.restaurantNotifications = restaurantNotifications;
        this.promotionalNotifications = promotionalNotifications;
        this.quietHoursEnabled = quietHoursEnabled;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.locale = locale;
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

    public Boolean isEmailEnabled() {
        return emailEnabled;
    }

    public Boolean isSmsEnabled() {
        return smsEnabled;
    }

    public Boolean isPushEnabled() {
        return pushEnabled;
    }

    public Boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public Boolean isOrderNotifications() {
        return orderNotifications;
    }

    public Boolean isPaymentNotifications() {
        return paymentNotifications;
    }

    public Boolean isRestaurantNotifications() {
        return restaurantNotifications;
    }

    public Boolean isPromotionalNotifications() {
        return promotionalNotifications;
    }

    public Boolean isQuietHoursEnabled() {
        return quietHoursEnabled;
    }

    public String getQuietHoursStart() {
        return quietHoursStart;
    }

    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLocale() {
        return locale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public static NotificationPreferenceResponse fromEntity(NotificationPreference preference) {
        return new NotificationPreferenceResponse(
            preference.getId(),
            preference.getUserId(),
            preference.getEmailEnabled(),
            preference.getSmsEnabled(),
            preference.getPushEnabled(),
            preference.getInAppEnabled(),
            preference.getOrderNotifications(),
            preference.getPaymentNotifications(),
            preference.getRestaurantNotifications(),
            preference.getPromotionalNotifications(),
            preference.getQuietHoursEnabled(),
            preference.getQuietHoursStart(),
            preference.getQuietHoursEnd(),
            preference.getEmailAddress(),
            preference.getPhoneNumber(),
            preference.getLocale(),
            preference.getCreatedAt(),
            preference.getUpdatedAt()
        );
    }
}
