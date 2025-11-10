package com.cookedspecially.reportingservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for category performance report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryPerformanceDTO {

    private String category;
    private Integer totalItemsSold;
    private BigDecimal totalRevenue;
    private BigDecimal percentageOfTotalRevenue;
    private Integer uniqueDishesOrdered;
    private BigDecimal averageDishPrice;
    private String trend;
}
