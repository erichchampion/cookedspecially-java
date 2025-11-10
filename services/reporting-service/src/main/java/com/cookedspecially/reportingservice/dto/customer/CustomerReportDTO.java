package com.cookedspecially.reportingservice.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for customer report data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerReportDTO {

    private Long customerId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime registrationDate;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private BigDecimal averageOrderValue;
    private LocalDateTime lastOrderDate;
    private String favoriteCategory;
    private String preferredPaymentMethod;
    private Integer loyaltyPoints;
    private String customerStatus;
}
