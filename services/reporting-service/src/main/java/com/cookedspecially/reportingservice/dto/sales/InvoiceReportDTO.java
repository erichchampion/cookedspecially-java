package com.cookedspecially.reportingservice.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for invoice report data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceReportDTO {

    private Long invoiceId;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String orderType;
    private String paymentMethod;
    private BigDecimal subtotal;
    private BigDecimal cgst;
    private BigDecimal sgst;
    private BigDecimal serviceTax;
    private BigDecimal deliveryCharge;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private String fulfillmentCenterName;
    private String restaurantName;
    private String status;
    private LocalDateTime createdAt;
}
