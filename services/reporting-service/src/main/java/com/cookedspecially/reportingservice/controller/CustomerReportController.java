package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.customer.CustomerReportDTO;
import com.cookedspecially.reportingservice.dto.customer.CustomerSummaryDTO;
import com.cookedspecially.reportingservice.service.CustomerReportService;
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
 * REST controller for customer reports.
 */
@RestController
@RequestMapping("/api/v1/reports/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Reports", description = "Customer reporting APIs")
public class CustomerReportController {

    private final CustomerReportService customerReportService;

    /**
     * Get customer list report.
     */
    @GetMapping("/list")
    @Operation(summary = "Customer list report", description = "Generate customer list report for a date range")
    public ResponseEntity<List<CustomerReportDTO>> getCustomerListReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId
    ) {
        log.info("Getting customer list report from {} to {}", fromDate, toDate);
        List<CustomerReportDTO> report = customerReportService.generateCustomerListReport(
            fromDate, toDate, restaurantId
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Get customer summary report.
     */
    @GetMapping("/summary")
    @Operation(summary = "Customer summary report", description = "Generate customer summary statistics")
    public ResponseEntity<CustomerSummaryDTO> getCustomerSummary(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId
    ) {
        log.info("Getting customer summary from {} to {}", fromDate, toDate);
        CustomerSummaryDTO summary = customerReportService.generateCustomerSummary(
            fromDate, toDate, restaurantId
        );
        return ResponseEntity.ok(summary);
    }

    /**
     * Get top customers by revenue.
     */
    @GetMapping("/top")
    @Operation(summary = "Top customers", description = "Get top customers by revenue")
    public ResponseEntity<List<CustomerReportDTO>> getTopCustomers(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.info("Getting top {} customers from {} to {}", limit, fromDate, toDate);
        List<CustomerReportDTO> customers = customerReportService.getTopCustomersByRevenue(
            fromDate, toDate, limit
        );
        return ResponseEntity.ok(customers);
    }
}
