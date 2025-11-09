package com.cookedspecially.paymentservice.domain;

/**
 * Payment Provider Enum
 *
 * Supported payment gateways/providers
 */
public enum PaymentProvider {
    /**
     * Stripe payment gateway
     */
    STRIPE,

    /**
     * PayPal payment gateway
     */
    PAYPAL,

    /**
     * Cash payment (for in-person orders)
     */
    CASH,

    /**
     * Gift card payment
     */
    GIFT_CARD,

    /**
     * Credit applied to account
     */
    STORE_CREDIT;

    /**
     * Check if provider requires external API call
     */
    public boolean requiresExternalApi() {
        return this == STRIPE || this == PAYPAL;
    }

    /**
     * Check if provider is offline payment method
     */
    public boolean isOfflinePayment() {
        return this == CASH;
    }
}
