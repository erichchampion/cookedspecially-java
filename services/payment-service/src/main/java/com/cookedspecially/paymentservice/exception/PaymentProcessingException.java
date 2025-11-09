package com.cookedspecially.paymentservice.exception;

/**
 * Exception thrown when payment processing fails
 */
public class PaymentProcessingException extends RuntimeException {

    private final String paymentNumber;
    private final String errorCode;

    public PaymentProcessingException(String message) {
        super(message);
        this.paymentNumber = null;
        this.errorCode = null;
    }

    public PaymentProcessingException(String paymentNumber, String message) {
        super(message);
        this.paymentNumber = paymentNumber;
        this.errorCode = null;
    }

    public PaymentProcessingException(String paymentNumber, String message, String errorCode) {
        super(message);
        this.paymentNumber = paymentNumber;
        this.errorCode = errorCode;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
