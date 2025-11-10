package com.cookedspecially.reportingservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a scheduled report configuration.
 */
@Entity
@Table(name = "scheduled_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reportName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(nullable = false)
    private String cronExpression;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportFormat format;

    @Column(nullable = false)
    private String recipientEmails;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "fulfillment_center_id")
    private Long fulfillmentCenterId;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_run_at")
    private LocalDateTime lastRunAt;

    @Column(name = "next_run_at")
    private LocalDateTime nextRunAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
