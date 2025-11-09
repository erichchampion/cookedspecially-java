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
 * Notification Entity
 */
@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    @Column(nullable = false, length = 500)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 200)
    private String recipient;  // Email address, phone number, or device token

    // Template information
    @Column(length = 100)
    private String templateId;

    @Column(columnDefinition = "JSON")
    private String templateVariables;

    // Related entity tracking
    @Column(length = 50)
    private String relatedEntityType;  // ORDER, PAYMENT, RESTAURANT

    private Long relatedEntityId;

    // Delivery tracking
    private LocalDateTime sentAt;

    private LocalDateTime deliveredAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private Integer retryCount;

    @Builder.Default
    private Integer maxRetries = 3;

    // External provider tracking
    @Column(length = 200)
    private String externalMessageId;  // SES Message ID, SNS Message ID, etc.

    @Column(length = 50)
    private String provider;  // SES, SNS_SMS, SNS_PUSH, etc.

    // Metadata
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Mark notification as sent
     */
    public void markAsSent(String externalMessageId, String provider) {
        this.status = NotificationStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.externalMessageId = externalMessageId;
        this.provider = provider;
    }

    /**
     * Mark notification as delivered
     */
    public void markAsDelivered() {
        this.status = NotificationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * Mark notification as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = NotificationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount = (this.retryCount == null) ? 1 : this.retryCount + 1;
    }

    /**
     * Check if notification can be retried
     */
    public boolean canRetry() {
        return this.retryCount == null || this.retryCount < this.maxRetries;
    }
}
