package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.Refund;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundResponse {

    private Long id;
    private String refundNumber;
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private String reason;
    private Refund.RefundStatus status;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static RefundResponse fromEntity(Refund refund) {
        return RefundResponse.builder()
            .id(refund.getId())
            .refundNumber(refund.getRefundNumber())
            .paymentId(refund.getPaymentId())
            .orderId(refund.getOrderId())
            .amount(refund.getAmount())
            .reason(refund.getReason())
            .status(refund.getStatus())
            .failureReason(refund.getFailureReason())
            .createdAt(refund.getCreatedAt())
            .completedAt(refund.getCompletedAt())
            .build();
    }
}
