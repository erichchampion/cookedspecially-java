package com.cookedspecially.reportingservice.dto.report;

import com.cookedspecially.reportingservice.domain.ExecutionStatus;
import com.cookedspecially.reportingservice.domain.ReportFormat;
import com.cookedspecially.reportingservice.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for report generation responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponseDTO {

    private Long reportId;
    private ReportType reportType;
    private ReportFormat format;
    private ExecutionStatus status;
    private String downloadUrl;
    private String fileName;
    private Long fileSizeBytes;
    private Integer rowCount;
    private LocalDateTime generatedAt;
    private Long executionDurationMs;
    private String errorMessage;
}
