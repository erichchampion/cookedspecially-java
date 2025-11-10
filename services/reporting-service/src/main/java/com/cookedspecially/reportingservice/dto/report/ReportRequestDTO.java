package com.cookedspecially.reportingservice.dto.report;

import com.cookedspecially.reportingservice.domain.ReportFormat;
import com.cookedspecially.reportingservice.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for report generation requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequestDTO {

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotNull(message = "Report format is required")
    private ReportFormat format;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private Long restaurantId;

    private Long fulfillmentCenterId;

    private Long customerId;

    private Integer limit;

    private Map<String, Object> additionalParameters;
}
