package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.service.SalesReportService;
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
 * REST controller for sales reports.
 */
@RestController
@RequestMapping("/api/v1/reports/sales")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sales Reports", description = "Sales reporting APIs")
public class SalesReportController {

    private final SalesReportService salesReportService;

    /**
     * Get daily invoice report.
     */
    @GetMapping("/daily-invoice")
    @Operation(summary = "Daily invoice report", description = "Generate daily invoice report for a date range")
    public ResponseEntity<List<InvoiceReportDTO>> getDailyInvoiceReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId,
        @RequestParam(required = false) Long fulfillmentCenterId
    ) {
        log.info("Getting daily invoice report from {} to {}", fromDate, toDate);
        List<InvoiceReportDTO> report = salesReportService.generateDailyInvoiceReport(
            fromDate, toDate, restaurantId, fulfillmentCenterId
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Get daily sales summary.
     */
    @GetMapping("/daily-summary")
    @Operation(summary = "Daily sales summary", description = "Generate daily sales summary for a date range")
    public ResponseEntity<List<DailySalesSummaryDTO>> getDailySalesSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long fulfillmentCenterId
    ) {
        log.info("Getting daily sales summary from {} to {}", fromDate, toDate);
        List<DailySalesSummaryDTO> report = salesReportService.generateDailySalesSummary(
            fromDate, toDate, fulfillmentCenterId
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Get detailed invoice report.
     */
    @GetMapping("/detailed-invoice")
    @Operation(summary = "Detailed invoice report", description = "Generate detailed invoice report with line items")
    public ResponseEntity<List<DetailedInvoiceReportDTO>> getDetailedInvoiceReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId
    ) {
        log.info("Getting detailed invoice report from {} to {}", fromDate, toDate);
        List<DetailedInvoiceReportDTO> report = salesReportService.generateDetailedInvoiceReport(
            fromDate, toDate, restaurantId
        );
        return ResponseEntity.ok(report);
    }
}
