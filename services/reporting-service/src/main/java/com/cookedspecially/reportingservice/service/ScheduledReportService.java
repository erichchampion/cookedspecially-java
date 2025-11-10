package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.domain.ScheduledReport;
import com.cookedspecially.reportingservice.dto.report.ReportRequestDTO;
import com.cookedspecially.reportingservice.dto.report.ReportResponseDTO;
import com.cookedspecially.reportingservice.repository.ScheduledReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing scheduled reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledReportService {

    private final ScheduledReportRepository scheduledReportRepository;
    private final ReportGenerationService reportGenerationService;
    private final EmailService emailService;

    /**
     * Create a new scheduled report.
     */
    @Transactional
    public ScheduledReport createScheduledReport(ScheduledReport scheduledReport) {
        log.info("Creating scheduled report: {}", scheduledReport.getReportName());
        return scheduledReportRepository.save(scheduledReport);
    }

    /**
     * Get all active scheduled reports.
     */
    public List<ScheduledReport> getActiveScheduledReports() {
        return scheduledReportRepository.findByActiveTrue();
    }

    /**
     * Update scheduled report.
     */
    @Transactional
    public ScheduledReport updateScheduledReport(Long id, ScheduledReport scheduledReport) {
        ScheduledReport existing = scheduledReportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Scheduled report not found: " + id));

        existing.setReportName(scheduledReport.getReportName());
        existing.setReportType(scheduledReport.getReportType());
        existing.setCronExpression(scheduledReport.getCronExpression());
        existing.setFormat(scheduledReport.getFormat());
        existing.setRecipientEmails(scheduledReport.getRecipientEmails());
        existing.setRestaurantId(scheduledReport.getRestaurantId());
        existing.setFulfillmentCenterId(scheduledReport.getFulfillmentCenterId());
        existing.setParameters(scheduledReport.getParameters());
        existing.setActive(scheduledReport.getActive());

        return scheduledReportRepository.save(existing);
    }

    /**
     * Delete scheduled report.
     */
    @Transactional
    public void deleteScheduledReport(Long id) {
        scheduledReportRepository.deleteById(id);
    }

    /**
     * Execute scheduled reports daily.
     */
    @Scheduled(cron = "${scheduled.reports.cron.daily:0 0 8 * * ?}")
    public void executeDailyReports() {
        log.info("Executing daily scheduled reports");
        executeScheduledReports("DAILY");
    }

    /**
     * Execute scheduled reports weekly.
     */
    @Scheduled(cron = "${scheduled.reports.cron.weekly:0 0 9 * * MON}")
    public void executeWeeklyReports() {
        log.info("Executing weekly scheduled reports");
        executeScheduledReports("WEEKLY");
    }

    /**
     * Execute scheduled reports monthly.
     */
    @Scheduled(cron = "${scheduled.reports.cron.monthly:0 0 9 1 * ?}")
    public void executeMonthlyReports() {
        log.info("Executing monthly scheduled reports");
        executeScheduledReports("MONTHLY");
    }

    private void executeScheduledReports(String frequency) {
        List<ScheduledReport> reports = scheduledReportRepository.findByActiveTrue();

        for (ScheduledReport report : reports) {
            if (shouldExecute(report, frequency)) {
                executeScheduledReport(report);
            }
        }
    }

    private boolean shouldExecute(ScheduledReport report, String frequency) {
        // Simple frequency check based on cron expression
        // In production, use a proper cron parser
        return report.getCronExpression().contains(frequency);
    }

    @Transactional
    protected void executeScheduledReport(ScheduledReport scheduledReport) {
        try {
            log.info("Executing scheduled report: {}", scheduledReport.getReportName());

            // Create report request
            ReportRequestDTO request = new ReportRequestDTO();
            request.setReportType(scheduledReport.getReportType());
            request.setFormat(scheduledReport.getFormat());
            request.setFromDate(LocalDate.now().minusDays(30));
            request.setToDate(LocalDate.now());
            request.setRestaurantId(scheduledReport.getRestaurantId());
            request.setFulfillmentCenterId(scheduledReport.getFulfillmentCenterId());

            // Generate report
            ReportResponseDTO response = reportGenerationService.generateReport(
                request,
                "SCHEDULED_REPORT_" + scheduledReport.getId()
            );

            // Send email if successful
            if (response.getStatus().name().equals("COMPLETED")) {
                emailService.sendReportEmail(
                    scheduledReport.getRecipientEmails().split(","),
                    scheduledReport.getReportName(),
                    response.getDownloadUrl()
                );
            }

            // Update last run time
            scheduledReport.setLastRunAt(LocalDateTime.now());
            scheduledReportRepository.save(scheduledReport);

        } catch (Exception e) {
            log.error("Error executing scheduled report: {}", e.getMessage(), e);
        }
    }
}
