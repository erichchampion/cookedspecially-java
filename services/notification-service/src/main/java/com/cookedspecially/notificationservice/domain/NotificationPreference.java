package com.cookedspecially.notificationservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Notification Preference Entity
 * Stores user preferences for receiving notifications
 */
@Entity
@Table(name = "notification_preferences", indexes = {
    @Index(name = "idx_user_id", columnList = "userId", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Channel preferences
    @Column(nullable = false)
    @Builder.Default
    private Boolean emailEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean smsEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean pushEnabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean inAppEnabled = true;

    // Type preferences
    @Column(nullable = false)
    @Builder.Default
    private Boolean orderNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean paymentNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean restaurantNotifications = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean promotionalNotifications = false;  // Opt-in for marketing

    // Quiet hours
    @Column(nullable = false)
    @Builder.Default
    private Boolean quietHoursEnabled = false;

    @Column(length = 5)
    private String quietHoursStart;  // Format: "22:00"

    @Column(length = 5)
    private String quietHoursEnd;  // Format: "08:00"

    // Contact information
    @Column(length = 200)
    private String emailAddress;

    @Column(length = 20)
    private String phoneNumber;

    // Device tokens for push notifications
    @Column(length = 500)
    private String androidDeviceToken;

    @Column(length = 500)
    private String iosDeviceToken;

    // Locale preference
    @Column(length = 10)
    @Builder.Default
    private String locale = "en";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if user can receive notification on channel
     */
    public boolean canReceiveOnChannel(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> emailEnabled && emailAddress != null;
            case SMS -> smsEnabled && phoneNumber != null;
            case PUSH -> pushEnabled && (androidDeviceToken != null || iosDeviceToken != null);
            case IN_APP -> inAppEnabled;
        };
    }

    /**
     * Check if user wants to receive notification type
     */
    public boolean wantsNotificationType(NotificationType type) {
        return switch (type) {
            case ORDER_PLACED, ORDER_CONFIRMED, ORDER_PREPARING,
                 ORDER_OUT_FOR_DELIVERY, ORDER_DELIVERED, ORDER_CANCELLED -> orderNotifications;
            case PAYMENT_RECEIVED, PAYMENT_FAILED, REFUND_PROCESSED -> paymentNotifications;
            case RESTAURANT_APPROVED, RESTAURANT_SUSPENDED,
                 RESTAURANT_REOPENED, MENU_UPDATED, NEW_REVIEW -> restaurantNotifications;
            case PROMOTIONAL -> promotionalNotifications;
            default -> true;  // System notifications always enabled
        };
    }
}
