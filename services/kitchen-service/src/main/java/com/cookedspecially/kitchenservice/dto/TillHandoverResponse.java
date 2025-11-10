package com.cookedspecially.kitchenservice.dto;

import com.cookedspecially.kitchenservice.domain.HandoverStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for till handover information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TillHandoverResponse {
    private Long id;
    private Long tillId;
    private String fromUserId;
    private String fromUserName;
    private String toUserId;
    private String toUserName;
    private BigDecimal expectedBalance;
    private BigDecimal actualBalance;
    private BigDecimal variance;
    private HandoverStatus status;
    private String notes;
    private String rejectionReason;
    private LocalDateTime handoverDate;
    private LocalDateTime approvedAt;
    private String approvedBy;
}
