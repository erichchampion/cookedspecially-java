package com.cookedspecially.kitchenservice.dto;

import com.cookedspecially.kitchenservice.domain.TillStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for till information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillResponse {
    private Long id;
    private String name;
    private String description;
    private Long fulfillmentCenterId;
    private Long restaurantId;
    private TillStatus status;
    private BigDecimal openingBalance;
    private BigDecimal currentBalance;
    private BigDecimal expectedBalance;
    private BigDecimal variance;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private String currentUserId;
    private String currentUserName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
