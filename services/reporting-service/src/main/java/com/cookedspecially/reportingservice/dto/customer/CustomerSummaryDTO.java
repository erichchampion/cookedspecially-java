package com.cookedspecially.reportingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for customer summary statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSummaryDTO {

    private Integer totalCustomers;
    private Integer newCustomersThisPeriod;
    private Integer activeCustomers;
    private Integer inactiveCustomers;
    private BigDecimal totalRevenue;
    private BigDecimal averageCustomerLifetimeValue;
    private BigDecimal averageOrderValue;
    private Double averageOrderFrequency;
    private Integer topCustomerCount;
}
