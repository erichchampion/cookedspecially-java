package com.cookedspecially.reportingservice.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for detailed invoice report with line items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedInvoiceReportDTO {

    private Long invoiceId;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String customerName;
    private String dishName;
    private String category;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String addOns;
    private BigDecimal discount;
    private BigDecimal tax;
    private String paymentMethod;
    private String fulfillmentCenterName;
}
