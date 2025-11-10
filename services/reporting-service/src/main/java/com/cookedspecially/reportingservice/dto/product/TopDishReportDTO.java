package com.cookedspecially.reportingservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for top dishes report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopDishReportDTO {

    private Long dishId;
    private String dishName;
    private String category;
    private Integer totalQuantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
    private Integer orderCount;
    private BigDecimal contributionToRevenue;
    private String trend;
}
