package com.cookedspecially.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notification Preference Request DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreferenceRequest {

    // Channel preferences
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean inAppEnabled;

    // Type preferences
    private Boolean orderNotifications;
    private Boolean paymentNotifications;
    private Boolean restaurantNotifications;
    private Boolean promotionalNotifications;

    // Quiet hours
    private Boolean quietHoursEnabled;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String quietHoursStart;

    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Invalid time format. Use HH:mm")
    private String quietHoursEnd;

    // Contact information
    @Email(message = "Invalid email format")
    private String emailAddress;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phoneNumber;

    // Device tokens
    private String androidDeviceToken;
    private String iosDeviceToken;

    // Locale
    private String locale;
}
