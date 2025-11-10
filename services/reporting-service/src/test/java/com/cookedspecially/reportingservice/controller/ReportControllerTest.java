package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.domain.ExecutionStatus;
import com.cookedspecially.reportingservice.domain.ReportFormat;
import com.cookedspecially.reportingservice.domain.ReportType;
import com.cookedspecially.reportingservice.dto.report.ReportRequestDTO;
import com.cookedspecially.reportingservice.dto.report.ReportResponseDTO;
import com.cookedspecially.reportingservice.service.ReportGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ReportController.
 */
@WebMvcTest(ReportController.class)
@ActiveProfiles("test")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportGenerationService reportGenerationService;

    @Test
    @WithMockUser(roles = "USER")
    void generateReport_WithValidRequest_ShouldReturn200() throws Exception {
        // Given
        ReportRequestDTO request = new ReportRequestDTO();
        request.setReportType(ReportType.DAILY_INVOICE);
        request.setFormat(ReportFormat.EXCEL);
        request.setFromDate(LocalDate.of(2025, 1, 1));
        request.setToDate(LocalDate.of(2025, 1, 31));

        ReportResponseDTO response = ReportResponseDTO.builder()
            .reportId(1L)
            .reportType(ReportType.DAILY_INVOICE)
            .format(ReportFormat.EXCEL)
            .status(ExecutionStatus.COMPLETED)
            .downloadUrl("https://s3.amazonaws.com/reports/test.xlsx")
            .fileName("test.xlsx")
            .fileSizeBytes(1024L)
            .generatedAt(LocalDateTime.now())
            .build();

        when(reportGenerationService.generateReport(any(ReportRequestDTO.class), any(String.class)))
            .thenReturn(response);

        // When/Then
        mockMvc.perform(post("/api/v1/reports/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.reportId").value(1))
            .andExpect(jsonPath("$.status").value("COMPLETED"))
            .andExpect(jsonPath("$.fileName").value("test.xlsx"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getReport_WithValidId_ShouldReturn200() throws Exception {
        // Given
        ReportResponseDTO response = ReportResponseDTO.builder()
            .reportId(1L)
            .reportType(ReportType.DAILY_INVOICE)
            .format(ReportFormat.EXCEL)
            .status(ExecutionStatus.COMPLETED)
            .downloadUrl("https://s3.amazonaws.com/reports/test.xlsx")
            .build();

        when(reportGenerationService.getReportHistory(1L)).thenReturn(response);

        // When/Then
        mockMvc.perform(get("/api/v1/reports/1")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.reportId").value(1))
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void generateReport_WithInvalidRequest_ShouldReturn400() throws Exception {
        // Given - Missing required fields
        ReportRequestDTO request = new ReportRequestDTO();

        // When/Then
        mockMvc.perform(post("/api/v1/reports/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}
