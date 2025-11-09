package com.cookedspecially.paymentservice.exception;

/**
 * Exception thrown when a payment is not found
 */
public class PaymentNotFoundException extends RuntimeException {

    private final Long paymentId;
    private final String paymentNumber;

    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
        this.paymentId = paymentId;
        this.paymentNumber = null;
    }

    public PaymentNotFoundException(String paymentNumber) {
        super("Payment not found with payment number: " + paymentNumber);
        this.paymentId = null;
        this.paymentNumber = paymentNumber;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }
}
