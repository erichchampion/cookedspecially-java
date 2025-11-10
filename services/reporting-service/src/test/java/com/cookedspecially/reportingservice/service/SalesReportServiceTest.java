package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.repository.SalesReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SalesReportService.
 */
@ExtendWith(MockitoExtension.class)
class SalesReportServiceTest {

    @Mock
    private SalesReportRepository salesReportRepository;

    @InjectMocks
    private SalesReportService salesReportService;

    private LocalDate fromDate;
    private LocalDate toDate;
    private Long restaurantId;
    private Long fulfillmentCenterId;

    @BeforeEach
    void setUp() {
        fromDate = LocalDate.of(2025, 1, 1);
        toDate = LocalDate.of(2025, 1, 31);
        restaurantId = 1L;
        fulfillmentCenterId = 1L;
    }

    @Test
    void generateDailyInvoiceReport_ShouldReturnInvoiceList() {
        // Given
        InvoiceReportDTO invoice = InvoiceReportDTO.builder()
            .invoiceId(1L)
            .invoiceNumber("INV-001")
            .invoiceDate(LocalDateTime.now())
            .customerName("John Doe")
            .customerPhone("1234567890")
            .customerEmail("john@example.com")
            .orderType("DELIVERY")
            .paymentMethod("CARD")
            .subtotal(BigDecimal.valueOf(100))
            .cgst(BigDecimal.valueOf(5))
            .sgst(BigDecimal.valueOf(5))
            .serviceTax(BigDecimal.valueOf(2))
            .deliveryCharge(BigDecimal.valueOf(10))
            .discount(BigDecimal.valueOf(5))
            .grandTotal(BigDecimal.valueOf(117))
            .fulfillmentCenterName("Main Kitchen")
            .restaurantName("Test Restaurant")
            .status("COMPLETED")
            .createdAt(LocalDateTime.now())
            .build();

        when(salesReportRepository.generateDailyInvoiceReport(fromDate, toDate, restaurantId, fulfillmentCenterId))
            .thenReturn(List.of(invoice));

        // When
        List<InvoiceReportDTO> result = salesReportService.generateDailyInvoiceReport(
            fromDate, toDate, restaurantId, fulfillmentCenterId
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInvoiceNumber()).isEqualTo("INV-001");
        assertThat(result.get(0).getGrandTotal()).isEqualByComparingTo(BigDecimal.valueOf(117));

        verify(salesReportRepository).generateDailyInvoiceReport(fromDate, toDate, restaurantId, fulfillmentCenterId);
    }

    @Test
    void generateDailySalesSummary_ShouldReturnSummaryList() {
        // Given
        DailySalesSummaryDTO summary = DailySalesSummaryDTO.builder()
            .date(LocalDate.of(2025, 1, 1))
            .fulfillmentCenterName("Main Kitchen")
            .totalOrders(50)
            .completedOrders(45)
            .cancelledOrders(5)
            .totalSales(BigDecimal.valueOf(5000))
            .cashSales(BigDecimal.valueOf(2000))
            .cardSales(BigDecimal.valueOf(2000))
            .onlineSales(BigDecimal.valueOf(1000))
            .totalTax(BigDecimal.valueOf(500))
            .totalDiscount(BigDecimal.valueOf(200))
            .netSales(BigDecimal.valueOf(4500))
            .averageOrderValue(BigDecimal.valueOf(100))
            .newCustomers(10)
            .returningCustomers(40)
            .build();

        when(salesReportRepository.generateDailySalesSummary(fromDate, toDate, fulfillmentCenterId))
            .thenReturn(List.of(summary));

        // When
        List<DailySalesSummaryDTO> result = salesReportService.generateDailySalesSummary(
            fromDate, toDate, fulfillmentCenterId
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalOrders()).isEqualTo(50);
        assertThat(result.get(0).getTotalSales()).isEqualByComparingTo(BigDecimal.valueOf(5000));

        verify(salesReportRepository).generateDailySalesSummary(fromDate, toDate, fulfillmentCenterId);
    }

    @Test
    void generateDetailedInvoiceReport_ShouldReturnDetailedList() {
        // Given
        DetailedInvoiceReportDTO detail = DetailedInvoiceReportDTO.builder()
            .invoiceNumber("INV-001")
            .invoiceDate(LocalDateTime.now())
            .customerName("John Doe")
            .dishName("Chicken Biryani")
            .category("Main Course")
            .quantity(2)
            .unitPrice(BigDecimal.valueOf(250))
            .lineTotal(BigDecimal.valueOf(500))
            .addOns("Extra Raita")
            .discount(BigDecimal.valueOf(50))
            .tax(BigDecimal.valueOf(45))
            .paymentMethod("CARD")
            .fulfillmentCenterName("Main Kitchen")
            .build();

        when(salesReportRepository.generateDetailedInvoiceReport(fromDate, toDate, restaurantId))
            .thenReturn(List.of(detail));

        // When
        List<DetailedInvoiceReportDTO> result = salesReportService.generateDetailedInvoiceReport(
            fromDate, toDate, restaurantId
        );

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDishName()).isEqualTo("Chicken Biryani");
        assertThat(result.get(0).getQuantity()).isEqualTo(2);

        verify(salesReportRepository).generateDetailedInvoiceReport(fromDate, toDate, restaurantId);
    }

    @Test
    void generateDailyInvoiceReport_WithEmptyResult_ShouldReturnEmptyList() {
        // Given
        when(salesReportRepository.generateDailyInvoiceReport(any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());

        // When
        List<InvoiceReportDTO> result = salesReportService.generateDailyInvoiceReport(
            fromDate, toDate, restaurantId, fulfillmentCenterId
        );

        // Then
        assertThat(result).isEmpty();
        verify(salesReportRepository).generateDailyInvoiceReport(fromDate, toDate, restaurantId, fulfillmentCenterId);
    }
}
