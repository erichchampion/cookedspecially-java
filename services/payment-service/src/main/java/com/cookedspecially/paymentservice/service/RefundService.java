package com.cookedspecially.paymentservice.service;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.PaymentProvider;
import com.cookedspecially.paymentservice.domain.Refund;
import com.cookedspecially.paymentservice.dto.CreateRefundRequest;
import com.cookedspecially.paymentservice.dto.RefundResponse;
import com.cookedspecially.paymentservice.exception.PaymentNotFoundException;
import com.cookedspecially.paymentservice.exception.RefundException;
import com.cookedspecially.paymentservice.repository.PaymentRepository;
import com.cookedspecially.paymentservice.repository.RefundRepository;
import com.cookedspecially.paymentservice.util.RefundNumberGenerator;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Refund Service - Handles payment refunds
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final RefundNumberGenerator refundNumberGenerator;
    // Event publisher will be injected later
    // private final PaymentEventPublisher eventPublisher;

    /**
     * Process a refund
     */
    @Transactional
    public RefundResponse processRefund(Long paymentId, CreateRefundRequest request) {
        log.info("Processing refund for payment: {}, amount: {}",
            paymentId, request.getAmount());

        // Validate payment
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        // Validate refund amount
        if (!payment.canBeRefunded()) {
            throw new RefundException(paymentId, "Payment cannot be refunded");
        }

        BigDecimal refundableAmount = payment.getRefundableAmount();
        if (request.getAmount().compareTo(refundableAmount) > 0) {
            throw new RefundException(paymentId,
                String.format("Refund amount %.2f exceeds refundable amount %.2f",
                    request.getAmount(), refundableAmount));
        }

        // Generate unique refund number
        String refundNumber = generateUniqueRefundNumber();

        // Create refund entity
        Refund refund = Refund.builder()
            .refundNumber(refundNumber)
            .paymentId(paymentId)
            .orderId(payment.getOrderId())
            .amount(request.getAmount())
            .reason(request.getReason())
            .status(Refund.RefundStatus.PENDING)
            .build();

        refund = refundRepository.save(refund);
        log.info("Refund created: {}", refundNumber);

        // Process refund based on payment provider
        try {
            switch (payment.getProvider()) {
                case STRIPE -> processStripeRefund(payment, refund);
                case PAYPAL -> processPayPalRefund(payment, refund);
                case CASH -> processCashRefund(payment, refund);
                case GIFT_CARD -> processGiftCardRefund(payment, refund);
                default -> throw new RefundException(paymentId,
                    "Unsupported payment provider: " + payment.getProvider());
            }

            // Update payment refunded amount
            payment.addRefundAmount(request.getAmount());
            paymentRepository.save(payment);

            refund = refundRepository.save(refund);
            log.info("Refund processed successfully: {}", refundNumber);

            // Publish refund completed event
            // eventPublisher.publishRefundCompleted(refund, payment);

        } catch (Exception e) {
            refund.markFailed(e.getMessage());
            refundRepository.save(refund);
            log.error("Refund processing failed: {}", refundNumber, e);

            // Publish refund failed event
            // eventPublisher.publishRefundFailed(refund, payment);

            throw new RefundException(paymentId, e.getMessage());
        }

        return RefundResponse.fromEntity(refund);
    }

    /**
     * Process Stripe refund
     */
    private void processStripeRefund(Payment payment, Refund refund) throws StripeException {
        log.info("Processing Stripe refund: {}", refund.getRefundNumber());

        long amountInCents = refund.getAmount().multiply(BigDecimal.valueOf(100)).longValue();

        RefundCreateParams params = RefundCreateParams.builder()
            .setPaymentIntent(payment.getExternalTransactionId())
            .setAmount(amountInCents)
            .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER)
            .putMetadata("refund_number", refund.getRefundNumber())
            .putMetadata("reason", refund.getReason())
            .build();

        com.stripe.model.Refund stripeRefund = com.stripe.model.Refund.create(params);

        refund.markCompleted(stripeRefund.getId());
    }

    /**
     * Process PayPal refund (stub)
     */
    private void processPayPalRefund(Payment payment, Refund refund) {
        log.info("Processing PayPal refund: {}", refund.getRefundNumber());
        refund.markCompleted("paypal_refund_" + System.currentTimeMillis());
    }

    /**
     * Process cash refund
     */
    private void processCashRefund(Payment payment, Refund refund) {
        log.info("Processing cash refund: {}", refund.getRefundNumber());
        refund.markCompleted("cash_refund_" + System.currentTimeMillis());
    }

    /**
     * Process gift card refund (stub)
     */
    private void processGiftCardRefund(Payment payment, Refund refund) {
        log.info("Processing gift card refund: {}", refund.getRefundNumber());
        refund.markCompleted("giftcard_refund_" + System.currentTimeMillis());
    }

    /**
     * Get refund by ID
     */
    @Transactional(readOnly = true)
    public RefundResponse getRefundById(Long refundId) {
        log.debug("Fetching refund by ID: {}", refundId);
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new RefundException("Refund not found with ID: " + refundId));
        return RefundResponse.fromEntity(refund);
    }

    /**
     * Get refunds for payment
     */
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsForPayment(Long paymentId) {
        log.debug("Fetching refunds for payment: {}", paymentId);
        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);
        return refunds.stream()
            .map(RefundResponse::fromEntity)
            .collect(Collectors.toList());
    }

    // ==================== Private Helper Methods ====================

    private String generateUniqueRefundNumber() {
        String refundNumber;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            refundNumber = refundNumberGenerator.generate();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new RefundException(
                    "Failed to generate unique refund number after " + maxAttempts + " attempts");
            }
        } while (refundRepository.findByRefundNumber(refundNumber).isPresent());

        return refundNumber;
    }
}
