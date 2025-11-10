package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for sales reports.
 * Note: This is a custom repository that will be implemented with native queries
 * or by connecting to other services' databases.
 */
@Repository
public interface SalesReportRepository {

    /**
     * Generate daily invoice report.
     */
    List<InvoiceReportDTO> generateDailyInvoiceReport(LocalDate fromDate, LocalDate toDate, Long restaurantId, Long fulfillmentCenterId);

    /**
     * Generate daily sales summary report.
     */
    List<DailySalesSummaryDTO> generateDailySalesSummary(LocalDate fromDate, LocalDate toDate, Long fulfillmentCenterId);

    /**
     * Generate detailed invoice report.
     */
    List<DetailedInvoiceReportDTO> generateDetailedInvoiceReport(LocalDate fromDate, LocalDate toDate, Long restaurantId);
}
