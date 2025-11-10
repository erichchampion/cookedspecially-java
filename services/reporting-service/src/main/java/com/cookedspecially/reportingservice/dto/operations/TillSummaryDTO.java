package com.cookedspecially.reportingservice.dto.operations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for till summary report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TillSummaryDTO {

    private Long tillId;
    private String tillName;
    private String fulfillmentCenterName;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String operatorName;
    private BigDecimal openingBalance;
    private BigDecimal totalCashSales;
    private BigDecimal totalCardSales;
    private BigDecimal totalOnlineSales;
    private BigDecimal cashAdded;
    private BigDecimal cashWithdrawn;
    private BigDecimal expectedClosingBalance;
    private BigDecimal actualClosingBalance;
    private BigDecimal variance;
    private Integer totalTransactions;
    private String status;
}
