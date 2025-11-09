package com.cookedspecially.paymentservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.PaymentStatus;
import com.cookedspecially.paymentservice.dto.CreatePaymentRequest;
import com.cookedspecially.paymentservice.dto.PaymentResponse;
import com.cookedspecially.paymentservice.exception.PaymentNotFoundException;
import com.cookedspecially.paymentservice.exception.PaymentProcessingException;
import com.cookedspecially.paymentservice.repository.PaymentRepository;
import com.cookedspecially.paymentservice.util.PaymentNumberGenerator;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment Service - Core business logic for payment processing
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentNumberGenerator paymentNumberGenerator;
    private final com.cookedspecially.paymentservice.event.PaymentEventPublisher eventPublisher;

    // Constructor
    public PaymentService(PaymentRepository paymentRepository,
                 PaymentNumberGenerator paymentNumberGenerator,
                 com.cookedspecially.paymentservice.event.PaymentEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.paymentNumberGenerator = paymentNumberGenerator;
        this.eventPublisher = eventPublisher;
    }

    @Value("${payment-service.stripe.api-key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe API initialized");
    }

    /**
     * Process a new payment
     */
    @Transactional
    @CircuitBreaker(name = "stripe", fallbackMethod = "processPaymentFallback")
    @Retry(name = "payment-processing")
    public PaymentResponse processPayment(CreatePaymentRequest request) {
        log.info("Processing payment for order: {}, amount: {} {}",
            request.orderId(), request.amount(), request.currency());

        // Generate unique payment number
        String paymentNumber = generateUniquePaymentNumber();

        // Create payment entity
        Payment payment = new Payment();
        payment.setPaymentNumber(paymentNumber);
        payment.setOrderId(request.orderId());
        payment.setCustomerId(request.customerId());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider(request.provider());
        payment.setAmount(request.amount());
        payment.setRefundedAmount(BigDecimal.ZERO);
        payment.setCurrency(request.currency().toUpperCase());
        payment.setDescription(request.description());
        payment.setMetadata(request.metadata());

        // Save initial payment
        payment = paymentRepository.save(payment);
        log.info("Payment created: {}", paymentNumber);

        // Process payment based on provider
        try {
            switch (request.provider()) {
                case STRIPE -> processStripePayment(payment);
                case PAYPAL -> processPayPalPayment(payment);
                case CASH -> processCashPayment(payment);
                case GIFT_CARD -> processGiftCardPayment(payment);
                default -> throw new PaymentProcessingException(
                    paymentNumber, "Unsupported payment provider: " + request.provider());
            }

            payment = paymentRepository.save(payment);
            log.info("Payment processed successfully: {}", paymentNumber);

            // Publish payment completed event (to be implemented)
            // eventPublisher.publishPaymentCompleted(payment);

        } catch (Exception e) {
            payment.markFailed(e.getMessage());
            payment = paymentRepository.save(payment);
            log.error("Payment processing failed: {}", paymentNumber, e);

            // Publish payment failed event
            // eventPublisher.publishPaymentFailed(payment);

            throw new PaymentProcessingException(paymentNumber, e.getMessage());
        }

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Process Stripe payment
     */
    private void processStripePayment(Payment payment) throws StripeException {
        log.info("Processing Stripe payment: {}", payment.getPaymentNumber());

        // Convert amount to cents
        long amountInCents = payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        // Create Stripe payment intent
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
            .setAmount(amountInCents)
            .setCurrency(payment.getCurrency().toLowerCase())
            .setDescription(payment.getDescription())
            .putMetadata("payment_number", payment.getPaymentNumber())
            .putMetadata("order_id", payment.getOrderId().toString())
            .putMetadata("customer_id", payment.getCustomerId().toString())
            .setConfirm(true)
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .build()
            )
            .build();

        PaymentIntent intent = PaymentIntent.create(params);

        // Update payment with Stripe details
        payment.setExternalTransactionId(intent.getId());
        payment.setStatus(PaymentStatus.PROCESSING);

        // Check payment status
        if ("succeeded".equals(intent.getStatus())) {
            payment.markCompleted(intent.getId());
        } else if ("requires_action".equals(intent.getStatus())) {
            payment.setStatus(PaymentStatus.REQUIRES_ACTION);
        } else if ("requires_payment_method".equals(intent.getStatus())) {
            payment.markFailed("Payment method required");
        }
    }

    /**
     * Process PayPal payment (stub implementation)
     */
    private void processPayPalPayment(Payment payment) {
        log.info("Processing PayPal payment: {}", payment.getPaymentNumber());
        // PayPal integration would go here
        payment.markCompleted("paypal_" + System.currentTimeMillis());
    }

    /**
     * Process cash payment
     */
    private void processCashPayment(Payment payment) {
        log.info("Processing cash payment: {}", payment.getPaymentNumber());
        payment.markCompleted("cash_" + System.currentTimeMillis());
    }

    /**
     * Process gift card payment (stub implementation)
     */
    private void processGiftCardPayment(Payment payment) {
        log.info("Processing gift card payment: {}", payment.getPaymentNumber());
        // Gift card validation logic would go here
        payment.markCompleted("giftcard_" + System.currentTimeMillis());
    }

    /**
     * Fallback method for circuit breaker
     */
    public PaymentResponse processPaymentFallback(CreatePaymentRequest request, Exception e) {
        log.error("Circuit breaker activated for payment processing", e);
        throw new PaymentProcessingException("Payment service temporarily unavailable. Please try again later.");
    }

    /**
     * Get payment by ID
     */
    @Cacheable(value = "payments", key = "#paymentId")
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        log.debug("Fetching payment by ID: {}", paymentId);
        Payment payment = findPaymentById(paymentId);
        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Get payment by payment number
     */
    @Cacheable(value = "payments", key = "#paymentNumber")
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByNumber(String paymentNumber) {
        log.debug("Fetching payment by number: {}", paymentNumber);
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
            .orElseThrow(() -> new PaymentNotFoundException(paymentNumber));
        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Get payment by order ID
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        log.debug("Fetching payment by order ID: {}", orderId);
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentNotFoundException("No payment found for order: " + orderId));
        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Get customer payments
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getCustomerPayments(Long customerId, Pageable pageable) {
        log.debug("Fetching payments for customer: {}", customerId);
        Page<Payment> payments = paymentRepository.findByCustomerId(customerId, pageable);
        return payments.map(PaymentResponse::fromEntity);
    }

    /**
     * Cancel payment
     */
    @Transactional
    @CacheEvict(value = "payments", key = "#paymentId")
    public PaymentResponse cancelPayment(Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus().isFinalState()) {
            throw new PaymentProcessingException(payment.getPaymentNumber(),
                "Cannot cancel payment in " + payment.getStatus() + " status");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        payment = paymentRepository.save(payment);

        // Cancel external payment if needed
        if (payment.getExternalTransactionId() != null) {
            cancelExternalPayment(payment);
        }

        // Publish payment cancelled event
        // eventPublisher.publishPaymentCancelled(payment);

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Cancel external payment
     */
    private void cancelExternalPayment(Payment payment) {
        try {
            if (payment.getProvider() == PaymentProvider.STRIPE) {
                PaymentIntent intent = PaymentIntent.retrieve(payment.getExternalTransactionId());
                intent.cancel();
                log.info("Cancelled Stripe payment: {}", payment.getExternalTransactionId());
            }
        } catch (StripeException e) {
            log.error("Failed to cancel external payment: {}", payment.getExternalTransactionId(), e);
        }
    }

    // ==================== Private Helper Methods ====================

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
    }

    private String generateUniquePaymentNumber() {
        String paymentNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            paymentNumber = paymentNumberGenerator.generate();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new PaymentProcessingException(
                    "Failed to generate unique payment number after " + maxAttempts + " attempts");
            }
        } while (paymentRepository.findByPaymentNumber(paymentNumber).isPresent());

        return paymentNumber;
    }
}
