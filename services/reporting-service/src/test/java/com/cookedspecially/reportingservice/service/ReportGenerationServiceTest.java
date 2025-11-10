package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.domain.ExecutionStatus;
import com.cookedspecially.reportingservice.domain.ReportExecutionHistory;
import com.cookedspecially.reportingservice.domain.ReportFormat;
import com.cookedspecially.reportingservice.domain.ReportType;
import com.cookedspecially.reportingservice.dto.report.ReportRequestDTO;
import com.cookedspecially.reportingservice.dto.report.ReportResponseDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.repository.ReportExecutionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReportGenerationService.
 */
@ExtendWith(MockitoExtension.class)
class ReportGenerationServiceTest {

    @Mock
    private ReportExecutionHistoryRepository executionHistoryRepository;

    @Mock
    private SalesReportService salesReportService;

    @Mock
    private CustomerReportService customerReportService;

    @Mock
    private ProductReportService productReportService;

    @Mock
    private OperationsReportService operationsReportService;

    @Mock
    private ExcelGenerationService excelGenerationService;

    @Mock
    private PdfGenerationService pdfGenerationService;

    @Mock
    private S3StorageService s3StorageService;

    @InjectMocks
    private ReportGenerationService reportGenerationService;

    private ReportRequestDTO request;
    private ReportExecutionHistory history;

    @BeforeEach
    void setUp() {
        request = new ReportRequestDTO();
        request.setReportType(ReportType.DAILY_INVOICE);
        request.setFormat(ReportFormat.EXCEL);
        request.setFromDate(LocalDate.of(2025, 1, 1));
        request.setToDate(LocalDate.of(2025, 1, 31));
        request.setRestaurantId(1L);
        request.setFulfillmentCenterId(1L);

        history = new ReportExecutionHistory();
        history.setId(1L);
        history.setReportType(ReportType.DAILY_INVOICE);
        history.setFormat(ReportFormat.EXCEL);
        history.setStatus(ExecutionStatus.RUNNING);
        history.setStartTime(LocalDateTime.now());
    }

    @Test
    void generateReport_WithExcelFormat_ShouldGenerateSuccessfully() throws Exception {
        // Given
        InvoiceReportDTO invoice = InvoiceReportDTO.builder()
            .invoiceId(1L)
            .invoiceNumber("INV-001")
            .grandTotal(BigDecimal.valueOf(100))
            .build();

        byte[] excelContent = "Excel content".getBytes();
        String s3Url = "https://s3.amazonaws.com/reports/test.xlsx";

        when(executionHistoryRepository.save(any(ReportExecutionHistory.class))).thenReturn(history);
        when(salesReportService.generateDailyInvoiceReport(any(), any(), any(), any()))
            .thenReturn(List.of(invoice));
        when(excelGenerationService.generateExcel(any(), any())).thenReturn(excelContent);
        when(s3StorageService.uploadReport(any(), any())).thenReturn(s3Url);

        // When
        ReportResponseDTO response = reportGenerationService.generateReport(request, "testUser");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReportType()).isEqualTo(ReportType.DAILY_INVOICE);
        assertThat(response.getFormat()).isEqualTo(ReportFormat.EXCEL);
        assertThat(response.getStatus()).isEqualTo(ExecutionStatus.COMPLETED);
        assertThat(response.getDownloadUrl()).isEqualTo(s3Url);

        verify(executionHistoryRepository, times(2)).save(any(ReportExecutionHistory.class));
        verify(salesReportService).generateDailyInvoiceReport(any(), any(), any(), any());
        verify(excelGenerationService).generateExcel(eq(ReportType.DAILY_INVOICE), any());
        verify(s3StorageService).uploadReport(any(), eq(excelContent));
    }

    @Test
    void generateReport_WithException_ShouldReturnFailedStatus() throws Exception {
        // Given
        when(executionHistoryRepository.save(any(ReportExecutionHistory.class))).thenReturn(history);
        when(salesReportService.generateDailyInvoiceReport(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("Database error"));

        // When
        ReportResponseDTO response = reportGenerationService.generateReport(request, "testUser");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(ExecutionStatus.FAILED);
        assertThat(response.getErrorMessage()).contains("Database error");

        verify(executionHistoryRepository, times(2)).save(any(ReportExecutionHistory.class));
    }

    @Test
    void getReportHistory_WithValidId_ShouldReturnReport() {
        // Given
        history.setStatus(ExecutionStatus.COMPLETED);
        history.setEndTime(LocalDateTime.now());
        history.setS3Url("https://s3.amazonaws.com/reports/test.xlsx");
        history.setFileName("test.xlsx");
        history.setFileSizeBytes(1024L);

        when(executionHistoryRepository.findById(1L)).thenReturn(Optional.of(history));

        // When
        ReportResponseDTO response = reportGenerationService.getReportHistory(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getReportId()).isEqualTo(1L);
        assertThat(response.getStatus()).isEqualTo(ExecutionStatus.COMPLETED);
        assertThat(response.getFileName()).isEqualTo("test.xlsx");

        verify(executionHistoryRepository).findById(1L);
    }

    @Test
    void getReportHistory_WithInvalidId_ShouldThrowException() {
        // Given
        when(executionHistoryRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        try {
            reportGenerationService.getReportHistory(999L);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Report not found");
        }

        verify(executionHistoryRepository).findById(999L);
    }
}
