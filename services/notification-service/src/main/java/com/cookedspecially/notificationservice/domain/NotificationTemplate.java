package com.cookedspecially.notificationservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Notification Template Entity
 */
@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_template_key", columnList = "templateKey", unique = true),
    @Index(name = "idx_type_channel", columnList = "type,channel")
})
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String templateKey;  // e.g., "ORDER_PLACED_EMAIL", "PAYMENT_RECEIVED_SMS"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false, length = 500)
    private String subject;  // For EMAIL channel

    @Column(nullable = false, columnDefinition = "TEXT")
    private String bodyTemplate;

    @Column(columnDefinition = "TEXT")
    private String htmlTemplate;  // HTML version for email

    @Column(columnDefinition = "TEXT")
    private String description;

    // Available variables in template (comma-separated)
    @Column(length = 1000)
    private String availableVariables;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(length = 10)
    private String locale;  // en, es, fr, etc.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public NotificationTemplate() {
    }

    public NotificationTemplate(Long id,
                 String templateKey,
                 NotificationType type,
                 NotificationChannel channel,
                 String subject,
                 String bodyTemplate,
                 String htmlTemplate,
                 String description,
                 String availableVariables,
                 Boolean isActive,
                 String locale,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.templateKey = templateKey;
        this.type = type;
        this.channel = channel;
        this.subject = subject;
        this.bodyTemplate = bodyTemplate;
        this.htmlTemplate = htmlTemplate;
        this.description = description;
        this.availableVariables = availableVariables;
        this.isActive = isActive != null ? isActive : true;
        this.locale = locale;
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

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvailableVariables() {
        return availableVariables;
    }

    public void setAvailableVariables(String availableVariables) {
        this.availableVariables = availableVariables;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
}
