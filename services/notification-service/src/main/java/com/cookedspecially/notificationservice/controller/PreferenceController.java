package com.cookedspecially.notificationservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.notificationservice.dto.NotificationPreferenceRequest;
import com.cookedspecially.notificationservice.dto.NotificationPreferenceResponse;
import com.cookedspecially.notificationservice.service.NotificationPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Notification Preference Controller
 */
@RestController
@RequestMapping("/api/v1/preferences")
@Tag(name = "Preference", description = "Notification preference management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class PreferenceController {

    private static final Logger log = LoggerFactory.getLogger(PreferenceController.class);

    private final NotificationPreferenceService preferenceService;

    // Constructor
    public PreferenceController(NotificationPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    /**
     * Get my preferences
     */
    @GetMapping
    @Operation(summary = "Get preferences", description = "Get notification preferences for current user")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching preferences for user: {}", userId);

        NotificationPreferenceResponse response = preferenceService.getPreferences(userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update my preferences
     */
    @PutMapping
    @Operation(summary = "Update preferences", description = "Update notification preferences")
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
        @Valid @RequestBody NotificationPreferenceRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Updating preferences for user: {}", userId);

        NotificationPreferenceResponse response = preferenceService.updatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete my preferences (reset to defaults)
     */
    @DeleteMapping
    @Operation(summary = "Delete preferences", description = "Reset preferences to default values")
    public ResponseEntity<Void> deletePreferences(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Deleting preferences for user: {}", userId);

        preferenceService.deletePreferences(userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Register device token for push notifications
     */
    @PostMapping("/device-token")
    @Operation(summary = "Register device token", description = "Register device token for push notifications")
    public ResponseEntity<Map<String, String>> registerDeviceToken(
        @RequestParam String deviceToken,
        @RequestParam(defaultValue = "false") boolean isIos,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Registering {} device token for user: {}", isIos ? "iOS" : "Android", userId);

        preferenceService.registerDeviceToken(userId, deviceToken, isIos);

        return ResponseEntity.ok(Map.of(
            "message", "Device token registered successfully",
            "platform", isIos ? "iOS" : "Android"
        ));
    }

    /**
     * Unregister device token
     */
    @DeleteMapping("/device-token")
    @Operation(summary = "Unregister device token", description = "Remove device token for push notifications")
    public ResponseEntity<Map<String, String>> unregisterDeviceToken(
        @RequestParam(defaultValue = "false") boolean isIos,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Unregistering {} device token for user: {}", isIos ? "iOS" : "Android", userId);

        preferenceService.unregisterDeviceToken(userId, isIos);

        return ResponseEntity.ok(Map.of(
            "message", "Device token unregistered successfully",
            "platform", isIos ? "iOS" : "Android"
        ));
    }

    /**
     * Enable/disable channel
     */
    @PatchMapping("/channels/{channel}")
    @Operation(summary = "Toggle channel", description = "Enable or disable notification channel")
    public ResponseEntity<NotificationPreferenceResponse> toggleChannel(
        @PathVariable String channel,
        @RequestParam boolean enabled,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Setting {} channel to {} for user: {}", channel, enabled, userId);

        NotificationPreferenceRequest request = switch (channel.toUpperCase()) {
            case "EMAIL" -> new NotificationPreferenceRequest(
                enabled, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            case "SMS" -> new NotificationPreferenceRequest(
                null, enabled, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
            case "PUSH" -> new NotificationPreferenceRequest(
                null, null, enabled, null, null, null, null, null, null, null, null, null, null, null, null, null);
            case "IN_APP" -> new NotificationPreferenceRequest(
                null, null, null, enabled, null, null, null, null, null, null, null, null, null, null, null, null);
            default -> null;
        };

        if (request == null) {
            return ResponseEntity.badRequest().build();
        }

        NotificationPreferenceResponse response = preferenceService.updatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Set quiet hours
     */
    @PatchMapping("/quiet-hours")
    @Operation(summary = "Set quiet hours", description = "Configure quiet hours for notifications")
    public ResponseEntity<NotificationPreferenceResponse> setQuietHours(
        @RequestParam boolean enabled,
        @RequestParam(required = false) String start,
        @RequestParam(required = false) String end,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Setting quiet hours for user {}: enabled={}, start={}, end={}",
            userId, enabled, start, end);

        NotificationPreferenceRequest request = new NotificationPreferenceRequest(
            null, // emailEnabled
            null, // smsEnabled
            null, // pushEnabled
            null, // inAppEnabled
            null, // orderNotifications
            null, // paymentNotifications
            null, // restaurantNotifications
            null, // promotionalNotifications
            enabled, // quietHoursEnabled
            start, // quietHoursStart
            end, // quietHoursEnd
            null, // emailAddress
            null, // phoneNumber
            null, // androidDeviceToken
            null, // iosDeviceToken
            null  // locale
        );

        NotificationPreferenceResponse response = preferenceService.updatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
}
