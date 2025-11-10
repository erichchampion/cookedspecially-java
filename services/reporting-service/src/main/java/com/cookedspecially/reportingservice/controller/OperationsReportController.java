package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.operations.DeliveryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.operations.TillSummaryDTO;
import com.cookedspecially.reportingservice.service.OperationsReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for operations reports.
 */
@RestController
@RequestMapping("/api/v1/reports/operations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Operations Reports", description = "Operations reporting APIs")
public class OperationsReportController {

    private final OperationsReportService operationsReportService;

    /**
     * Get delivery performance report.
     */
    @GetMapping("/delivery-performance")
    @Operation(summary = "Delivery performance report", description = "Generate delivery performance report")
    public ResponseEntity<List<DeliveryPerformanceDTO>> getDeliveryPerformanceReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId
    ) {
        log.info("Getting delivery performance report from {} to {}", fromDate, toDate);
        List<DeliveryPerformanceDTO> report = operationsReportService.generateDeliveryPerformanceReport(
            fromDate, toDate, restaurantId
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Get till summary report.
     */
    @GetMapping("/till-summary")
    @Operation(summary = "Till summary report", description = "Generate till summary report")
    public ResponseEntity<List<TillSummaryDTO>> getTillSummaryReport(
        @RequestParam Long tillId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        log.info("Getting till summary report for till {} from {} to {}", tillId, fromDate, toDate);
        List<TillSummaryDTO> report = operationsReportService.generateTillSummaryReport(
            tillId, fromDate, toDate
        );
        return ResponseEntity.ok(report);
    }
}
