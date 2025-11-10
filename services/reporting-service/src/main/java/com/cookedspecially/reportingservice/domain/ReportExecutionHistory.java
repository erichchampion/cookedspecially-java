package com.cookedspecially.reportingservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing the execution history of a report.
 */
@Entity
@Table(name = "report_execution_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduled_report_id")
    private Long scheduledReportId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportFormat format;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "execution_duration_ms")
    private Long executionDurationMs;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "s3_url")
    private String s3Url;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "generated_by")
    private String generatedBy;

    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (status == null) {
            status = ExecutionStatus.RUNNING;
        }
    }
}
