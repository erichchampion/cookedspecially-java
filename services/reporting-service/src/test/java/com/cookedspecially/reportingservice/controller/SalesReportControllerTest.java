package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.service.SalesReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SalesReportController.
 */
@WebMvcTest(SalesReportController.class)
@ActiveProfiles("test")
class SalesReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalesReportService salesReportService;

    @Test
    @WithMockUser(roles = "USER")
    void getDailyInvoiceReport_ShouldReturnInvoices() throws Exception {
        // Given
        InvoiceReportDTO invoice = InvoiceReportDTO.builder()
            .invoiceId(1L)
            .invoiceNumber("INV-001")
            .invoiceDate(LocalDateTime.now())
            .customerName("John Doe")
            .grandTotal(BigDecimal.valueOf(100))
            .status("COMPLETED")
            .build();

        when(salesReportService.generateDailyInvoiceReport(any(), any(), any(), any()))
            .thenReturn(List.of(invoice));

        // When/Then
        mockMvc.perform(get("/api/v1/reports/sales/daily-invoice")
                .param("fromDate", "2025-01-01")
                .param("toDate", "2025-01-31")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].invoiceNumber").value("INV-001"))
            .andExpect(jsonPath("$[0].customerName").value("John Doe"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getDailySalesSummary_ShouldReturnSummary() throws Exception {
        // Given
        DailySalesSummaryDTO summary = DailySalesSummaryDTO.builder()
            .date(LocalDate.of(2025, 1, 1))
            .fulfillmentCenterName("Main Kitchen")
            .totalOrders(50)
            .completedOrders(45)
            .totalSales(BigDecimal.valueOf(5000))
            .build();

        when(salesReportService.generateDailySalesSummary(any(), any(), any()))
            .thenReturn(List.of(summary));

        // When/Then
        mockMvc.perform(get("/api/v1/reports/sales/daily-summary")
                .param("fromDate", "2025-01-01")
                .param("toDate", "2025-01-31")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].totalOrders").value(50))
            .andExpect(jsonPath("$[0].completedOrders").value(45));
    }

    @Test
    void getDailyInvoiceReport_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/v1/reports/sales/daily-invoice")
                .param("fromDate", "2025-01-01")
                .param("toDate", "2025-01-31"))
            .andExpect(status().isUnauthorized());
    }
}
