package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.dto.customer.CustomerReportDTO;
import com.cookedspecially.reportingservice.dto.customer.CustomerSummaryDTO;
import com.cookedspecially.reportingservice.repository.CustomerReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for generating customer reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerReportService {

    private final CustomerReportRepository customerReportRepository;

    /**
     * Generate customer list report.
     */
    @Cacheable(value = "customerListReport", key = "#fromDate + '-' + #toDate + '-' + #restaurantId")
    public List<CustomerReportDTO> generateCustomerListReport(LocalDate fromDate, LocalDate toDate, Long restaurantId) {
        log.info("Generating customer list report from {} to {}", fromDate, toDate);
        return customerReportRepository.generateCustomerListReport(fromDate, toDate, restaurantId);
    }

    /**
     * Generate customer summary report.
     */
    @Cacheable(value = "customerSummary", key = "#fromDate + '-' + #toDate + '-' + #restaurantId")
    public CustomerSummaryDTO generateCustomerSummary(LocalDate fromDate, LocalDate toDate, Long restaurantId) {
        log.info("Generating customer summary from {} to {}", fromDate, toDate);
        return customerReportRepository.generateCustomerSummary(fromDate, toDate, restaurantId);
    }

    /**
     * Get top customers by revenue.
     */
    @Cacheable(value = "topCustomers", key = "#fromDate + '-' + #toDate + '-' + #limit")
    public List<CustomerReportDTO> getTopCustomersByRevenue(LocalDate fromDate, LocalDate toDate, Integer limit) {
        log.info("Getting top {} customers by revenue from {} to {}", limit, fromDate, toDate);
        return customerReportRepository.getTopCustomersByRevenue(fromDate, toDate, limit);
    }
}
