package com.cookedspecially.notificationservice.service;

import com.cookedspecially.notificationservice.domain.NotificationPreference;
import com.cookedspecially.notificationservice.dto.NotificationPreferenceRequest;
import com.cookedspecially.notificationservice.dto.NotificationPreferenceResponse;
import com.cookedspecially.notificationservice.repository.NotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notification Preference Service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;

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
        if (request.getEmailEnabled() != null) {
            preference.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getSmsEnabled() != null) {
            preference.setSmsEnabled(request.getSmsEnabled());
        }
        if (request.getPushEnabled() != null) {
            preference.setPushEnabled(request.getPushEnabled());
        }
        if (request.getInAppEnabled() != null) {
            preference.setInAppEnabled(request.getInAppEnabled());
        }
        if (request.getOrderNotifications() != null) {
            preference.setOrderNotifications(request.getOrderNotifications());
        }
        if (request.getPaymentNotifications() != null) {
            preference.setPaymentNotifications(request.getPaymentNotifications());
        }
        if (request.getRestaurantNotifications() != null) {
            preference.setRestaurantNotifications(request.getRestaurantNotifications());
        }
        if (request.getPromotionalNotifications() != null) {
            preference.setPromotionalNotifications(request.getPromotionalNotifications());
        }
        if (request.getQuietHoursEnabled() != null) {
            preference.setQuietHoursEnabled(request.getQuietHoursEnabled());
        }
        if (request.getQuietHoursStart() != null) {
            preference.setQuietHoursStart(request.getQuietHoursStart());
        }
        if (request.getQuietHoursEnd() != null) {
            preference.setQuietHoursEnd(request.getQuietHoursEnd());
        }
        if (request.getEmailAddress() != null) {
            preference.setEmailAddress(request.getEmailAddress());
        }
        if (request.getPhoneNumber() != null) {
            preference.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAndroidDeviceToken() != null) {
            preference.setAndroidDeviceToken(request.getAndroidDeviceToken());
        }
        if (request.getIosDeviceToken() != null) {
            preference.setIosDeviceToken(request.getIosDeviceToken());
        }
        if (request.getLocale() != null) {
            preference.setLocale(request.getLocale());
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

        NotificationPreference preference = NotificationPreference.builder()
            .userId(userId)
            .build();

        return preferenceRepository.save(preference);
    }
}
