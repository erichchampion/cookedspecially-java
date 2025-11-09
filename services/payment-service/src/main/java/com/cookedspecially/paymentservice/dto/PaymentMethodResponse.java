package com.cookedspecially.paymentservice.dto;

import com.cookedspecially.paymentservice.domain.PaymentMethod;
import com.cookedspecially.paymentservice.domain.PaymentMethodType;
import com.cookedspecially.paymentservice.domain.PaymentProvider;

import java.time.LocalDateTime;

/**
 * Payment Method Response DTO
 */
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

    // Constructors
    public PaymentMethodResponse() {
    }

    public PaymentMethodResponse(Long id,
                 Long customerId,
                 PaymentMethodType type,
                 PaymentProvider provider,
                 String cardBrand,
                 String cardLast4,
                 String cardExpMonth,
                 String cardExpYear,
                 String maskedCardNumber,
                 Boolean isDefault,
                 Boolean isActive,
                 Boolean isExpired,
                 LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.provider = provider;
        this.cardBrand = cardBrand;
        this.cardLast4 = cardLast4;
        this.cardExpMonth = cardExpMonth;
        this.cardExpYear = cardExpYear;
        this.maskedCardNumber = maskedCardNumber;
        this.isDefault = isDefault;
        this.isActive = isActive;
        this.isExpired = isExpired;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public PaymentMethodType getType() {
        return type;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public String getCardExpMonth() {
        return cardExpMonth;
    }

    public String getCardExpYear() {
        return cardExpYear;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public Boolean isIsDefault() {
        return isDefault;
    }

    public Boolean isIsActive() {
        return isActive;
    }

    public Boolean isIsExpired() {
        return isExpired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setType(PaymentMethodType type) {
        this.type = type;
    }

    public void setProvider(PaymentProvider provider) {
        this.provider = provider;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public void setCardExpMonth(String cardExpMonth) {
        this.cardExpMonth = cardExpMonth;
    }

    public void setCardExpYear(String cardExpYear) {
        this.cardExpYear = cardExpYear;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsExpired(Boolean isExpired) {
        this.isExpired = isExpired;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public static PaymentMethodResponse fromEntity(PaymentMethod paymentMethod) {
        PaymentMethodResponse response = new PaymentMethodResponse();
        response.setId(paymentMethod.getId());
        response.setCustomerId(paymentMethod.getCustomerId());
        response.setType(paymentMethod.getType());
        response.setProvider(paymentMethod.getProvider());
        response.setCardBrand(paymentMethod.getCardBrand());
        response.setCardLast4(paymentMethod.getCardLast4());
        response.setCardExpMonth(paymentMethod.getCardExpMonth());
        response.setCardExpYear(paymentMethod.getCardExpYear());
        response.setMaskedCardNumber(paymentMethod.getMaskedCardNumber());
        response.setIsDefault(paymentMethod.getIsDefault());
        response.setIsActive(paymentMethod.getIsActive());
        response.setIsExpired(paymentMethod.isExpired());
        response.setCreatedAt(paymentMethod.getCreatedAt());
        return response;
    }
}
