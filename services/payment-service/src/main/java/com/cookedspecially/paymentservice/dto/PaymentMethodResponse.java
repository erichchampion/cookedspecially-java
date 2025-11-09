package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.PaymentMethod;
import com.cookedspecially.paymentservice.domain.PaymentMethodType;
import com.cookedspecially.paymentservice.domain.PaymentProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payment Method Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {

    private Long id;
    private Long customerId;
    private PaymentMethodType type;
    private PaymentProvider provider;
    private String cardBrand;
    private String cardLast4;
    private String cardExpMonth;
    private String cardExpYear;
    private String maskedCardNumber;
    private Boolean isDefault;
    private Boolean isActive;
    private Boolean isExpired;
    private LocalDateTime createdAt;

    public static PaymentMethodResponse fromEntity(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
            .id(paymentMethod.getId())
            .customerId(paymentMethod.getCustomerId())
            .type(paymentMethod.getType())
            .provider(paymentMethod.getProvider())
            .cardBrand(paymentMethod.getCardBrand())
            .cardLast4(paymentMethod.getCardLast4())
            .cardExpMonth(paymentMethod.getCardExpMonth())
            .cardExpYear(paymentMethod.getCardExpYear())
            .maskedCardNumber(paymentMethod.getMaskedCardNumber())
            .isDefault(paymentMethod.getIsDefault())
            .isActive(paymentMethod.getIsActive())
            .isExpired(paymentMethod.isExpired())
            .createdAt(paymentMethod.getCreatedAt())
            .build();
    }
}
