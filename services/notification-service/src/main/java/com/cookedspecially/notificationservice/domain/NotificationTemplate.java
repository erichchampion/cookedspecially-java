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
 * Notification Template Entity
 */
@Entity
@Table(name = "notification_templates", indexes = {
    @Index(name = "idx_template_key", columnList = "templateKey", unique = true),
    @Index(name = "idx_type_channel", columnList = "type,channel")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Boolean isActive = true;

    @Column(length = 10)
    private String locale;  // en, es, fr, etc.

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
