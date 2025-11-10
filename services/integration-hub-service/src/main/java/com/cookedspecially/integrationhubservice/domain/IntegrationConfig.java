package com.cookedspecially.integrationhubservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "integration_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private IntegrationType type;

    @Column(nullable = false, unique = true, length = 100)
    private String partnerId;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(length = 500)
    private String apiKey;

    @Column(length = 500)
    private String apiSecret;

    @Column(length = 500)
    private String webhookUrl;

    @Column(length = 500)
    private String webhookSecret;

    @Column(columnDefinition = "TEXT")
    private String configuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IntegrationStatus status;

    @Column
    private LocalDateTime lastHealthCheckAt;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enabled == null) {
            enabled = true;
        }
        if (status == null) {
            status = IntegrationStatus.INACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
