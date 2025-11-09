package com.cookedspecially.notificationservice.domain;

import jakarta.persistence.*;
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

    // Constructors
    public Notification() {
    }

    public Notification(Long id,
                 Long userId,
                 NotificationType type,
                 NotificationChannel channel,
                 NotificationStatus status,
                 NotificationPriority priority,
                 String subject,
                 String content,
                 String recipient,
                 String templateId,
                 String templateVariables,
                 String relatedEntityType,
                 Long relatedEntityId,
                 LocalDateTime sentAt,
                 LocalDateTime deliveredAt,
                 String errorMessage,
                 Integer retryCount,
                 Integer maxRetries,
                 String externalMessageId,
                 String provider,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.channel = channel;
        this.status = status;
        this.priority = priority != null ? priority : NotificationPriority.MEDIUM;
        this.subject = subject;
        this.content = content;
        this.recipient = recipient;
        this.templateId = templateId;
        this.templateVariables = templateVariables;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.maxRetries = maxRetries != null ? maxRetries : 3;
        this.externalMessageId = externalMessageId;
        this.provider = provider;
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

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateVariables() {
        return templateVariables;
    }

    public void setTemplateVariables(String templateVariables) {
        this.templateVariables = templateVariables;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getExternalMessageId() {
        return externalMessageId;
    }

    public void setExternalMessageId(String externalMessageId) {
        this.externalMessageId = externalMessageId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
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
