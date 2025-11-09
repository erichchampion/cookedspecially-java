package com.cookedspecially.notificationservice.domain;

import jakarta.persistence.*;
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
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Channel preferences
    @Column(nullable = false)
    private Boolean emailEnabled = true;

    @Column(nullable = false)
    private Boolean smsEnabled = true;

    @Column(nullable = false)
    private Boolean pushEnabled = true;

    @Column(nullable = false)
    private Boolean inAppEnabled = true;

    // Type preferences
    @Column(nullable = false)
    private Boolean orderNotifications = true;

    @Column(nullable = false)
    private Boolean paymentNotifications = true;

    @Column(nullable = false)
    private Boolean restaurantNotifications = true;

    @Column(nullable = false)
    private Boolean promotionalNotifications = false;  // Opt-in for marketing

    // Quiet hours
    @Column(nullable = false)
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
    private String locale = "en";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public NotificationPreference() {
    }

    public NotificationPreference(Long id,
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
                 String androidDeviceToken,
                 String iosDeviceToken,
                 String locale,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.emailEnabled = emailEnabled != null ? emailEnabled : true;
        this.smsEnabled = smsEnabled != null ? smsEnabled : true;
        this.pushEnabled = pushEnabled != null ? pushEnabled : true;
        this.inAppEnabled = inAppEnabled != null ? inAppEnabled : true;
        this.orderNotifications = orderNotifications != null ? orderNotifications : true;
        this.paymentNotifications = paymentNotifications != null ? paymentNotifications : true;
        this.restaurantNotifications = restaurantNotifications != null ? restaurantNotifications : true;
        this.promotionalNotifications = promotionalNotifications != null ? promotionalNotifications : false;
        this.quietHoursEnabled = quietHoursEnabled != null ? quietHoursEnabled : false;
        this.quietHoursStart = quietHoursStart;
        this.quietHoursEnd = quietHoursEnd;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.androidDeviceToken = androidDeviceToken;
        this.iosDeviceToken = iosDeviceToken;
        this.locale = locale != null ? locale : "en";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean isEmailEnabled() {
        return emailEnabled;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public Boolean isSmsEnabled() {
        return smsEnabled;
    }

    public Boolean getSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(Boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }

    public Boolean isPushEnabled() {
        return pushEnabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public Boolean isInAppEnabled() {
        return inAppEnabled;
    }

    public Boolean getInAppEnabled() {
        return inAppEnabled;
    }

    public void setInAppEnabled(Boolean inAppEnabled) {
        this.inAppEnabled = inAppEnabled;
    }

    public Boolean isOrderNotifications() {
        return orderNotifications;
    }

    public Boolean getOrderNotifications() {
        return orderNotifications;
    }

    public void setOrderNotifications(Boolean orderNotifications) {
        this.orderNotifications = orderNotifications;
    }

    public Boolean isPaymentNotifications() {
        return paymentNotifications;
    }

    public Boolean getPaymentNotifications() {
        return paymentNotifications;
    }

    public void setPaymentNotifications(Boolean paymentNotifications) {
        this.paymentNotifications = paymentNotifications;
    }

    public Boolean isRestaurantNotifications() {
        return restaurantNotifications;
    }

    public Boolean getRestaurantNotifications() {
        return restaurantNotifications;
    }

    public void setRestaurantNotifications(Boolean restaurantNotifications) {
        this.restaurantNotifications = restaurantNotifications;
    }

    public Boolean isPromotionalNotifications() {
        return promotionalNotifications;
    }

    public Boolean getPromotionalNotifications() {
        return promotionalNotifications;
    }

    public void setPromotionalNotifications(Boolean promotionalNotifications) {
        this.promotionalNotifications = promotionalNotifications;
    }

    public Boolean isQuietHoursEnabled() {
        return quietHoursEnabled;
    }

    public Boolean getQuietHoursEnabled() {
        return quietHoursEnabled;
    }

    public void setQuietHoursEnabled(Boolean quietHoursEnabled) {
        this.quietHoursEnabled = quietHoursEnabled;
    }

    public String getQuietHoursStart() {
        return quietHoursStart;
    }

    public void setQuietHoursStart(String quietHoursStart) {
        this.quietHoursStart = quietHoursStart;
    }

    public String getQuietHoursEnd() {
        return quietHoursEnd;
    }

    public void setQuietHoursEnd(String quietHoursEnd) {
        this.quietHoursEnd = quietHoursEnd;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAndroidDeviceToken() {
        return androidDeviceToken;
    }

    public void setAndroidDeviceToken(String androidDeviceToken) {
        this.androidDeviceToken = androidDeviceToken;
    }

    public String getIosDeviceToken() {
        return iosDeviceToken;
    }

    public void setIosDeviceToken(String iosDeviceToken) {
        this.iosDeviceToken = iosDeviceToken;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

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
