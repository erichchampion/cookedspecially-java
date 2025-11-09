package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentNumber;
    private Long orderId;
    private Long customerId;
    private PaymentStatus status;
    private PaymentProvider provider;
    private BigDecimal amount;
    private BigDecimal refundedAmount;
    private String currency;
    private String description;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
            .id(payment.getId())
            .paymentNumber(payment.getPaymentNumber())
            .orderId(payment.getOrderId())
            .customerId(payment.getCustomerId())
            .status(payment.getStatus())
            .provider(payment.getProvider())
            .amount(payment.getAmount())
            .refundedAmount(payment.getRefundedAmount())
            .currency(payment.getCurrency())
            .description(payment.getDescription())
            .failureReason(payment.getFailureReason())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .completedAt(payment.getCompletedAt())
            .build();
    }
}
