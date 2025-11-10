package com.cookedspecially.reportingservice.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for daily sales summary report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySalesSummaryDTO {

    private LocalDate date;
    private String fulfillmentCenterName;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer cancelledOrders;
    private BigDecimal totalSales;
    private BigDecimal cashSales;
    private BigDecimal cardSales;
    private BigDecimal onlineSales;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal netSales;
    private BigDecimal averageOrderValue;
    private Integer newCustomers;
    private Integer returningCustomers;
}
