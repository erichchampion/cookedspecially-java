package com.cookedspecially.reportingservice.repository;

import com.cookedspecially.reportingservice.dto.product.CategoryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.product.TopDishReportDTO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for product reports.
 */
@Repository
public interface ProductReportRepository {

    /**
     * Generate top dishes report.
     */
    List<TopDishReportDTO> generateTopDishesReport(LocalDate fromDate, LocalDate toDate, Long restaurantId, Integer limit);

    /**
     * Generate category performance report.
     */
    List<CategoryPerformanceDTO> generateCategoryPerformanceReport(LocalDate fromDate, LocalDate toDate, Long restaurantId);
}
