package com.cookedspecially.reportingservice.service;

import com.cookedspecially.reportingservice.dto.product.CategoryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.product.TopDishReportDTO;
import com.cookedspecially.reportingservice.repository.ProductReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for generating product reports.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductReportService {

    private final ProductReportRepository productReportRepository;

    /**
     * Generate top dishes report.
     */
    @Cacheable(value = "topDishesReport", key = "#fromDate + '-' + #toDate + '-' + #restaurantId + '-' + #limit")
    public List<TopDishReportDTO> generateTopDishesReport(LocalDate fromDate, LocalDate toDate,
                                                          Long restaurantId, Integer limit) {
        log.info("Generating top dishes report from {} to {}, limit: {}", fromDate, toDate, limit);
        return productReportRepository.generateTopDishesReport(fromDate, toDate, restaurantId, limit);
    }

    /**
     * Generate category performance report.
     */
    @Cacheable(value = "categoryPerformance", key = "#fromDate + '-' + #toDate + '-' + #restaurantId")
    public List<CategoryPerformanceDTO> generateCategoryPerformanceReport(LocalDate fromDate, LocalDate toDate,
                                                                          Long restaurantId) {
        log.info("Generating category performance report from {} to {}", fromDate, toDate);
        return productReportRepository.generateCategoryPerformanceReport(fromDate, toDate, restaurantId);
    }
}
