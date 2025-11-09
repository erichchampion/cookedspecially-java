package com.cookedspecially.notificationservice.controller;

import com.cookedspecially.notificationservice.domain.NotificationStatus;
import com.cookedspecially.notificationservice.dto.NotificationResponse;
import com.cookedspecially.notificationservice.dto.SendNotificationRequest;
import com.cookedspecially.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

/**
 * Notification Controller
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification", description = "Notification management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Send notification (admin only for manual sending)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Send notification", description = "Manually send notification (admin only)")
    public ResponseEntity<NotificationResponse> sendNotification(
        @Valid @RequestBody SendNotificationRequest request
    ) {
        log.info("Sending {} notification to user {} via {}",
            request.getType(), request.getUserId(), request.getChannel());

        NotificationResponse response = notificationService.sendNotification(request);

        if (response == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieve notification details")
    public ResponseEntity<NotificationResponse> getNotification(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching notification {} for user {}", id, userId);

        NotificationResponse response = notificationService.getNotificationById(id);

        // Verify user owns this notification
        if (!response.getUserId().equals(userId) && !hasRole(jwt, "ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Get my notifications
     */
    @GetMapping("/my-notifications")
    @Operation(summary = "Get my notifications", description = "Get all notifications for current user")
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
        @AuthenticationPrincipal Jwt jwt,
        Pageable pageable
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching notifications for user: {}", userId);

        Page<NotificationResponse> notifications = notificationService.getUserNotifications(userId, pageable);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get my notifications by status
     */
    @GetMapping("/my-notifications/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Filter notifications by status")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByStatus(
        @PathVariable NotificationStatus status,
        @AuthenticationPrincipal Jwt jwt,
        Pageable pageable
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching {} notifications for user: {}", status, userId);

        Page<NotificationResponse> notifications = notificationService
            .getUserNotificationsByStatus(userId, status, pageable);

        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark notification as read
     */
    @PostMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark in-app notification as read")
    public ResponseEntity<Void> markAsRead(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Marking notification {} as read for user {}", id, userId);

        // Verify ownership
        NotificationResponse notification = notificationService.getNotificationById(id);
        if (!notification.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Get unread count
     */
    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread in-app notifications")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.debug("Getting unread count for user: {}", userId);

        Page<NotificationResponse> unread = notificationService
            .getUserNotificationsByStatus(userId, NotificationStatus.SENT, Pageable.ofSize(1));

        return ResponseEntity.ok(unread.getTotalElements());
    }

    /**
     * Helper to check if user has role
     */
    private boolean hasRole(Jwt jwt, String role) {
        return jwt.getClaimAsStringList("roles") != null &&
               jwt.getClaimAsStringList("roles").contains(role);
    }
}
