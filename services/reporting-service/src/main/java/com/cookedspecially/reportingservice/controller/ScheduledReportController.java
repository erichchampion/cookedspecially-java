package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.domain.ScheduledReport;
import com.cookedspecially.reportingservice.service.ScheduledReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for scheduled reports.
 */
@RestController
@RequestMapping("/api/v1/reports/scheduled")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Scheduled Reports", description = "Scheduled report management APIs")
public class ScheduledReportController {

    private final ScheduledReportService scheduledReportService;

    /**
     * Create a scheduled report.
     */
    @PostMapping
    @Operation(summary = "Create scheduled report", description = "Create a new scheduled report")
    public ResponseEntity<ScheduledReport> createScheduledReport(
        @Valid @RequestBody ScheduledReport scheduledReport
    ) {
        log.info("Creating scheduled report: {}", scheduledReport.getReportName());
        ScheduledReport created = scheduledReportService.createScheduledReport(scheduledReport);
        return ResponseEntity.ok(created);
    }

    /**
     * Get all active scheduled reports.
     */
    @GetMapping
    @Operation(summary = "Get scheduled reports", description = "Get all active scheduled reports")
    public ResponseEntity<List<ScheduledReport>> getScheduledReports() {
        log.info("Getting all scheduled reports");
        List<ScheduledReport> reports = scheduledReportService.getActiveScheduledReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Update a scheduled report.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update scheduled report", description = "Update an existing scheduled report")
    public ResponseEntity<ScheduledReport> updateScheduledReport(
        @PathVariable Long id,
        @Valid @RequestBody ScheduledReport scheduledReport
    ) {
        log.info("Updating scheduled report: {}", id);
        ScheduledReport updated = scheduledReportService.updateScheduledReport(id, scheduledReport);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a scheduled report.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete scheduled report", description = "Delete a scheduled report")
    public ResponseEntity<Void> deleteScheduledReport(@PathVariable Long id) {
        log.info("Deleting scheduled report: {}", id);
        scheduledReportService.deleteScheduledReport(id);
        return ResponseEntity.noContent().build();
    }
}
