package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.dto.customer.CustomerReportDTO;
import com.cookedspecially.reportingservice.dto.customer.CustomerSummaryDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for customer reports.
 */
@Repository
public interface CustomerReportRepository {

    /**
     * Generate customer list report.
     */
    List<CustomerReportDTO> generateCustomerListReport(LocalDate fromDate, LocalDate toDate, Long restaurantId);

    /**
     * Generate customer summary report.
     */
    CustomerSummaryDTO generateCustomerSummary(LocalDate fromDate, LocalDate toDate, Long restaurantId);

    /**
     * Get top customers by revenue.
     */
    List<CustomerReportDTO> getTopCustomersByRevenue(LocalDate fromDate, LocalDate toDate, Integer limit);
}
