package com.cookedspecially.reportingservice.dto.operations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for delivery performance report.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryPerformanceDTO {

    private Long deliveryPersonId;
    private String deliveryPersonName;
    private Integer totalDeliveries;
    private Integer onTimeDeliveries;
    private Integer lateDeliveries;
    private Double onTimePercentage;
    private Double averageDeliveryTimeMinutes;
    private BigDecimal totalDeliveryChargesCollected;
    private Integer customerRatingCount;
    private Double averageRating;
    private String performanceStatus;
}
