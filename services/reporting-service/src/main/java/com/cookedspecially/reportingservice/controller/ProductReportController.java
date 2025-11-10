package com.cookedspecially.reportingservice.controller;

import com.cookedspecially.reportingservice.dto.product.CategoryPerformanceDTO;
import com.cookedspecially.reportingservice.dto.product.TopDishReportDTO;
import com.cookedspecially.reportingservice.service.ProductReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for product reports.
 */
@RestController
@RequestMapping("/api/v1/reports/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Reports", description = "Product reporting APIs")
public class ProductReportController {

    private final ProductReportService productReportService;

    /**
     * Get top dishes report.
     */
    @GetMapping("/top-dishes")
    @Operation(summary = "Top dishes report", description = "Generate top dishes report by sales")
    public ResponseEntity<List<TopDishReportDTO>> getTopDishesReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId,
        @RequestParam(defaultValue = "10") Integer limit
    ) {
        log.info("Getting top dishes report from {} to {}, limit: {}", fromDate, toDate, limit);
        List<TopDishReportDTO> report = productReportService.generateTopDishesReport(
            fromDate, toDate, restaurantId, limit
        );
        return ResponseEntity.ok(report);
    }

    /**
     * Get category performance report.
     */
    @GetMapping("/category-performance")
    @Operation(summary = "Category performance report", description = "Generate category performance report")
    public ResponseEntity<List<CategoryPerformanceDTO>> getCategoryPerformanceReport(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) Long restaurantId
    ) {
        log.info("Getting category performance report from {} to {}", fromDate, toDate);
        List<CategoryPerformanceDTO> report = productReportService.generateCategoryPerformanceReport(
            fromDate, toDate, restaurantId
        );
        return ResponseEntity.ok(report);
    }
}
