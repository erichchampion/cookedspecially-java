package com.cookedspecially.paymentservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Payment Method Entity
 *
 * Represents a stored payment method for a customer
 */
@Entity
@Table(name = "payment_methods", indexes = {
    @Index(name = "idx_customer_id", columnList = "customerId"),
    @Index(name = "idx_external_id", columnList = "externalPaymentMethodId")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethodType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentProvider provider;

    @Column(length = 255)
    private String externalPaymentMethodId;

    @Column(length = 100)
    private String cardBrand;

    @Column(length = 4)
    private String cardLast4;

    @Column(length = 2)
    private String cardExpMonth;

    @Column(length = 4)
    private String cardExpYear;

    @Column(length = 255)
    private String billingName;

    @Column(length = 500)
    private String billingAddress;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Get masked card number for display
     */
    public String getMaskedCardNumber() {
        if (cardLast4 != null) {
            return "**** **** **** " + cardLast4;
        }
        return null;
    }

    /**
     * Check if card is expired
     */
    public boolean isExpired() {
        if (cardExpMonth == null || cardExpYear == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        int expYear = Integer.parseInt(cardExpYear);
        int expMonth = Integer.parseInt(cardExpMonth);

        return expYear < currentYear || (expYear == currentYear && expMonth < currentMonth);
    }
}
