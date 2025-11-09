package com.cookedspecially.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

/**
 * Notification Preference Request DTO
 */
public record NotificationPreferenceRequest(
    Boolean emailEnabled,

    Boolean smsEnabled,

    Boolean pushEnabled,

    Boolean inAppEnabled,

    Boolean orderNotifications,

    Boolean paymentNotifications,

    Boolean restaurantNotifications,

    Boolean promotionalNotifications,

    Boolean quietHoursEnabled,

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    String quietHoursStart,

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    String quietHoursEnd,

    @Email(message = "Invalid email format")
    String emailAddress,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    String androidDeviceToken,

    String iosDeviceToken,

    String locale
) {}
