package com.cookedspecially.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.notificationservice.domain.NotificationPreference;
import com.cookedspecially.notificationservice.dto.NotificationPreferenceRequest;
import com.cookedspecially.notificationservice.dto.NotificationPreferenceResponse;
import com.cookedspecially.notificationservice.repository.NotificationPreferenceRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notification Preference Service
 */
@Service
public class NotificationPreferenceService {

    private static final Logger log = LoggerFactory.getLogger(NotificationPreferenceService.class);

    private final NotificationPreferenceRepository preferenceRepository;

    // Constructor
    public NotificationPreferenceService(NotificationPreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    /**
     * Get user preferences
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "preferences", key = "#userId")
    public NotificationPreferenceResponse getPreferences(Long userId) {
        log.debug("Fetching preferences for user: {}", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreferences(userId));

        return NotificationPreferenceResponse.fromEntity(preference);
    }

    /**
     * Update user preferences
     */
    @Transactional
    @CacheEvict(value = "preferences", key = "#userId")
    public NotificationPreferenceResponse updatePreferences(Long userId, NotificationPreferenceRequest request) {
        log.info("Updating preferences for user: {}", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreferences(userId));

        // Update fields if provided
        if (request.emailEnabled() != null) {
            preference.setEmailEnabled(request.emailEnabled());
        }
        if (request.smsEnabled() != null) {
            preference.setSmsEnabled(request.smsEnabled());
        }
        if (request.pushEnabled() != null) {
            preference.setPushEnabled(request.pushEnabled());
        }
        if (request.inAppEnabled() != null) {
            preference.setInAppEnabled(request.inAppEnabled());
        }
        if (request.orderNotifications() != null) {
            preference.setOrderNotifications(request.orderNotifications());
        }
        if (request.paymentNotifications() != null) {
            preference.setPaymentNotifications(request.paymentNotifications());
        }
        if (request.restaurantNotifications() != null) {
            preference.setRestaurantNotifications(request.restaurantNotifications());
        }
        if (request.promotionalNotifications() != null) {
            preference.setPromotionalNotifications(request.promotionalNotifications());
        }
        if (request.quietHoursEnabled() != null) {
            preference.setQuietHoursEnabled(request.quietHoursEnabled());
        }
        if (request.quietHoursStart() != null) {
            preference.setQuietHoursStart(request.quietHoursStart());
        }
        if (request.quietHoursEnd() != null) {
            preference.setQuietHoursEnd(request.quietHoursEnd());
        }
        if (request.emailAddress() != null) {
            preference.setEmailAddress(request.emailAddress());
        }
        if (request.phoneNumber() != null) {
            preference.setPhoneNumber(request.phoneNumber());
        }
        if (request.androidDeviceToken() != null) {
            preference.setAndroidDeviceToken(request.androidDeviceToken());
        }
        if (request.iosDeviceToken() != null) {
            preference.setIosDeviceToken(request.iosDeviceToken());
        }
        if (request.locale() != null) {
            preference.setLocale(request.locale());
        }

        NotificationPreference updated = preferenceRepository.save(preference);
        log.info("Updated preferences for user: {}", userId);

        return NotificationPreferenceResponse.fromEntity(updated);
    }

    /**
     * Delete user preferences
     */
    @Transactional
    @CacheEvict(value = "preferences", key = "#userId")
    public void deletePreferences(Long userId) {
        log.info("Deleting preferences for user: {}", userId);
        preferenceRepository.deleteByUserId(userId);
    }

    /**
     * Register device token for push notifications
     */
    @Transactional
    @CacheEvict(value = "preferences", key = "#userId")
    public void registerDeviceToken(Long userId, String deviceToken, boolean isIos) {
        log.info("Registering {} device token for user: {}", isIos ? "iOS" : "Android", userId);

        NotificationPreference preference = preferenceRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultPreferences(userId));

        if (isIos) {
            preference.setIosDeviceToken(deviceToken);
        } else {
            preference.setAndroidDeviceToken(deviceToken);
        }

        preference.setPushEnabled(true);
        preferenceRepository.save(preference);

        log.info("Device token registered successfully");
    }

    /**
     * Unregister device token
     */
    @Transactional
    @CacheEvict(value = "preferences", key = "#userId")
    public void unregisterDeviceToken(Long userId, boolean isIos) {
        log.info("Unregistering {} device token for user: {}", isIos ? "iOS" : "Android", userId);

        preferenceRepository.findByUserId(userId).ifPresent(preference -> {
            if (isIos) {
                preference.setIosDeviceToken(null);
            } else {
                preference.setAndroidDeviceToken(null);
            }

            // Disable push if no tokens remain
            if (preference.getAndroidDeviceToken() == null && preference.getIosDeviceToken() == null) {
                preference.setPushEnabled(false);
            }

            preferenceRepository.save(preference);
            log.info("Device token unregistered successfully");
        });
    }

    /**
     * Create default preferences for new user
     */
    private NotificationPreference createDefaultPreferences(Long userId) {
        log.info("Creating default preferences for user: {}", userId);

        NotificationPreference preference = new NotificationPreference(
            null, // id
            userId,
            true, // emailEnabled
            true, // smsEnabled
            true, // pushEnabled
            true, // inAppEnabled
            true, // orderNotifications
            true, // paymentNotifications
            true, // restaurantNotifications
            false, // promotionalNotifications
            false, // quietHoursEnabled
            null, // quietHoursStart
            null, // quietHoursEnd
            null, // emailAddress
            null, // phoneNumber
            null, // androidDeviceToken
            null, // iosDeviceToken
            "en", // locale
            null, // createdAt
            null  // updatedAt
        );

        return preferenceRepository.save(preference);
    }
}
