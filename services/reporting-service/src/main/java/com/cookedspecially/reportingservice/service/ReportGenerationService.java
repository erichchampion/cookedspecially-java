package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.domain.ExecutionStatus;
import com.cookedspecially.reportingservice.domain.ReportExecutionHistory;
import com.cookedspecially.reportingservice.dto.report.ReportRequestDTO;
import com.cookedspecially.reportingservice.dto.report.ReportResponseDTO;
import com.cookedspecially.reportingservice.repository.ReportExecutionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for generating various types of reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationService {

    private final ReportExecutionHistoryRepository executionHistoryRepository;
    private final SalesReportService salesReportService;
    private final CustomerReportService customerReportService;
    private final ProductReportService productReportService;
    private final OperationsReportService operationsReportService;
    private final ExcelGenerationService excelGenerationService;
    private final PdfGenerationService pdfGenerationService;
    private final S3StorageService s3StorageService;

    /**
     * Generate a report based on the request.
     */
    @Transactional
    public ReportResponseDTO generateReport(ReportRequestDTO request, String generatedBy) {
        log.info("Generating report: {} in format: {}", request.getReportType(), request.getFormat());

        // Create execution history record
        ReportExecutionHistory history = new ReportExecutionHistory();
        history.setReportType(request.getReportType());
        history.setFormat(request.getFormat());
        history.setStatus(ExecutionStatus.RUNNING);
        history.setStartTime(LocalDateTime.now());
        history.setGeneratedBy(generatedBy);
        history.setParameters(buildParametersString(request));
        history = executionHistoryRepository.save(history);

        try {
            // Generate report data
            Object reportData = generateReportData(request);

            // Generate file based on format
            byte[] fileContent;
            String fileName;
            switch (request.getFormat()) {
                case EXCEL -> {
                    fileName = generateFileName(request, "xlsx");
                    fileContent = excelGenerationService.generateExcel(request.getReportType(), reportData);
                }
                case PDF -> {
                    fileName = generateFileName(request, "pdf");
                    fileContent = pdfGenerationService.generatePdf(request.getReportType(), reportData);
                }
                default -> throw new IllegalArgumentException("Unsupported format: " + request.getFormat());
            }

            // Upload to S3
            String s3Url = s3StorageService.uploadReport(fileName, fileContent);

            // Update execution history
            history.setEndTime(LocalDateTime.now());
            history.setExecutionDurationMs(
                java.time.Duration.between(history.getStartTime(), history.getEndTime()).toMillis()
            );
            history.setStatus(ExecutionStatus.COMPLETED);
            history.setS3Url(s3Url);
            history.setFileName(fileName);
            history.setFileSizeBytes((long) fileContent.length);
            executionHistoryRepository.save(history);

            log.info("Report generated successfully: {}", fileName);

            return ReportResponseDTO.builder()
                .reportId(history.getId())
                .reportType(request.getReportType())
                .format(request.getFormat())
                .status(ExecutionStatus.COMPLETED)
                .downloadUrl(s3Url)
                .fileName(fileName)
                .fileSizeBytes((long) fileContent.length)
                .generatedAt(history.getEndTime())
                .executionDurationMs(history.getExecutionDurationMs())
                .build();

        } catch (Exception e) {
            log.error("Error generating report: {}", e.getMessage(), e);

            // Update execution history with error
            history.setEndTime(LocalDateTime.now());
            history.setStatus(ExecutionStatus.FAILED);
            history.setErrorMessage(e.getMessage());
            executionHistoryRepository.save(history);

            return ReportResponseDTO.builder()
                .reportId(history.getId())
                .reportType(request.getReportType())
                .format(request.getFormat())
                .status(ExecutionStatus.FAILED)
                .errorMessage(e.getMessage())
                .build();
        }
    }

    /**
     * Generate report data based on report type.
     */
    private Object generateReportData(ReportRequestDTO request) {
        return switch (request.getReportType()) {
            case DAILY_INVOICE -> salesReportService.generateDailyInvoiceReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId(), request.getFulfillmentCenterId()
            );
            case DAILY_SALES_SUMMARY -> salesReportService.generateDailySalesSummary(
                request.getFromDate(), request.getToDate(), request.getFulfillmentCenterId()
            );
            case DETAILED_INVOICE -> salesReportService.generateDetailedInvoiceReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId()
            );
            case CUSTOMER_LIST -> customerReportService.generateCustomerListReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId()
            );
            case CUSTOMER_SUMMARY -> customerReportService.generateCustomerSummary(
                request.getFromDate(), request.getToDate(), request.getRestaurantId()
            );
            case TOP_DISHES -> productReportService.generateTopDishesReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId(), request.getLimit()
            );
            case CATEGORY_PERFORMANCE -> productReportService.generateCategoryPerformanceReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId()
            );
            case DELIVERY_PERFORMANCE -> operationsReportService.generateDeliveryPerformanceReport(
                request.getFromDate(), request.getToDate(), request.getRestaurantId()
            );
            default -> throw new IllegalArgumentException("Unsupported report type: " + request.getReportType());
        };
    }

    /**
     * Get report execution history.
     */
    @Cacheable(value = "reportHistory", key = "#reportId")
    public ReportResponseDTO getReportHistory(Long reportId) {
        ReportExecutionHistory history = executionHistoryRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        return ReportResponseDTO.builder()
            .reportId(history.getId())
            .reportType(history.getReportType())
            .format(history.getFormat())
            .status(history.getStatus())
            .downloadUrl(history.getS3Url())
            .fileName(history.getFileName())
            .fileSizeBytes(history.getFileSizeBytes())
            .generatedAt(history.getEndTime())
            .executionDurationMs(history.getExecutionDurationMs())
            .errorMessage(history.getErrorMessage())
            .build();
    }

    private String generateFileName(ReportRequestDTO request, String extension) {
        return String.format("%s_%s_%s.%s",
            request.getReportType().name().toLowerCase(),
            request.getFromDate(),
            request.getToDate(),
            extension
        );
    }

    private String buildParametersString(ReportRequestDTO request) {
        return String.format("fromDate=%s, toDate=%s, restaurantId=%s, fcId=%s",
            request.getFromDate(), request.getToDate(),
            request.getRestaurantId(), request.getFulfillmentCenterId()
        );
    }
}
