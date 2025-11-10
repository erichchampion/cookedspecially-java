package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.dto.operations.DeliveryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.operations.TillSummaryDTO;
import com.cookedspecially.reportingservice.repository.OperationsReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for generating operations reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OperationsReportService {

    private final OperationsReportRepository operationsReportRepository;

    /**
     * Generate delivery performance report.
     */
    @Cacheable(value = "deliveryPerformance", key = "#fromDate + '-' + #toDate + '-' + #restaurantId")
    public List<DeliveryPerformanceDTO> generateDeliveryPerformanceReport(LocalDate fromDate, LocalDate toDate,
                                                                          Long restaurantId) {
        log.info("Generating delivery performance report from {} to {}", fromDate, toDate);
        return operationsReportRepository.generateDeliveryPerformanceReport(fromDate, toDate, restaurantId);
    }

    /**
     * Generate till summary report.
     */
    @Cacheable(value = "tillSummary", key = "#tillId + '-' + #fromDate + '-' + #toDate")
    public List<TillSummaryDTO> generateTillSummaryReport(Long tillId, LocalDate fromDate, LocalDate toDate) {
        log.info("Generating till summary report for till {} from {} to {}", tillId, fromDate, toDate);
        return operationsReportRepository.generateTillSummaryReport(tillId, fromDate, toDate);
    }
}
