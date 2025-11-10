package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.dto.sales.DailySalesSummaryDTO;
import com.cookedspecially.reportingservice.dto.sales.DetailedInvoiceReportDTO;
import com.cookedspecially.reportingservice.dto.sales.InvoiceReportDTO;
import com.cookedspecially.reportingservice.repository.SalesReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for generating sales reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SalesReportService {

    private final SalesReportRepository salesReportRepository;

    /**
     * Generate daily invoice report.
     */
    @Cacheable(value = "dailyInvoiceReport", key = "#fromDate + '-' + #toDate + '-' + #restaurantId + '-' + #fulfillmentCenterId")
    public List<InvoiceReportDTO> generateDailyInvoiceReport(LocalDate fromDate, LocalDate toDate,
                                                             Long restaurantId, Long fulfillmentCenterId) {
        log.info("Generating daily invoice report from {} to {}", fromDate, toDate);
        return salesReportRepository.generateDailyInvoiceReport(fromDate, toDate, restaurantId, fulfillmentCenterId);
    }

    /**
     * Generate daily sales summary.
     */
    @Cacheable(value = "dailySalesSummary", key = "#fromDate + '-' + #toDate + '-' + #fulfillmentCenterId")
    public List<DailySalesSummaryDTO> generateDailySalesSummary(LocalDate fromDate, LocalDate toDate,
                                                                Long fulfillmentCenterId) {
        log.info("Generating daily sales summary from {} to {}", fromDate, toDate);
        return salesReportRepository.generateDailySalesSummary(fromDate, toDate, fulfillmentCenterId);
    }

    /**
     * Generate detailed invoice report.
     */
    @Cacheable(value = "detailedInvoiceReport", key = "#fromDate + '-' + #toDate + '-' + #restaurantId")
    public List<DetailedInvoiceReportDTO> generateDetailedInvoiceReport(LocalDate fromDate, LocalDate toDate,
                                                                        Long restaurantId) {
        log.info("Generating detailed invoice report from {} to {}", fromDate, toDate);
        return salesReportRepository.generateDetailedInvoiceReport(fromDate, toDate, restaurantId);
    }
}
