package com.cookedspecially.integrationhubservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "social_connectors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialConnector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ConnectorType type;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(length = 500)
    private String accessToken;

    @Column(length = 500)
    private String refreshToken;

    @Column
    private LocalDateTime tokenExpiresAt;

    @Column(columnDefinition = "TEXT")
    private String configuration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConnectorStatus status;

    @Column
    private LocalDateTime lastSyncAt;

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
            status = ConnectorStatus.INACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
