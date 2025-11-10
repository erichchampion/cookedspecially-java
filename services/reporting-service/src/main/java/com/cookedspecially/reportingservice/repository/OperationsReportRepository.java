package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.dto.operations.DeliveryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.operations.TillSummaryDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for operations reports.
 */
@Repository
public interface OperationsReportRepository {

    /**
     * Generate delivery performance report.
     */
    List<DeliveryPerformanceDTO> generateDeliveryPerformanceReport(LocalDate fromDate, LocalDate toDate, Long restaurantId);

    /**
     * Generate till summary report.
     */
    List<TillSummaryDTO> generateTillSummaryReport(Long tillId, LocalDate fromDate, LocalDate toDate);
}
