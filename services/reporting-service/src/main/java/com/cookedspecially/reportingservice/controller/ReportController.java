package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.report.ReportRequestDTO;
import com.cookedspecially.reportingservice.dto.report.ReportResponseDTO;
import com.cookedspecially.reportingservice.service.ReportGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for report generation and management.
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reports", description = "Report generation and management APIs")
public class ReportController {

    private final ReportGenerationService reportGenerationService;

    /**
     * Generate a report.
     */
    @PostMapping("/generate")
    @Operation(summary = "Generate a report", description = "Generate a report based on the provided parameters")
    public ResponseEntity<ReportResponseDTO> generateReport(
        @Valid @RequestBody ReportRequestDTO request,
        Authentication authentication
    ) {
        log.info("Generating report: {}", request.getReportType());
        String generatedBy = authentication != null ? authentication.getName() : "anonymous";
        ReportResponseDTO response = reportGenerationService.generateReport(request, generatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Get report execution history by ID.
     */
    @GetMapping("/{reportId}")
    @Operation(summary = "Get report by ID", description = "Retrieve report execution history and download link")
    public ResponseEntity<ReportResponseDTO> getReport(@PathVariable Long reportId) {
        log.info("Getting report: {}", reportId);
        ReportResponseDTO response = reportGenerationService.getReportHistory(reportId);
        return ResponseEntity.ok(response);
    }
}
