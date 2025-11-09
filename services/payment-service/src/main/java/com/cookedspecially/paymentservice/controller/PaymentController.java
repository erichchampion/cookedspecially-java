package com.cookedspecially.paymentservice.controller;

import com.cookedspecially.paymentservice.dto.CreatePaymentRequest;
import com.cookedspecially.paymentservice.dto.CreateRefundRequest;
import com.cookedspecially.paymentservice.dto.PaymentResponse;
import com.cookedspecially.paymentservice.dto.RefundResponse;
import com.cookedspecially.paymentservice.service.PaymentService;
import com.cookedspecially.paymentservice.service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Payment REST API Controller
 *
 * Provides RESTful endpoints for payment processing and management
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payments", description = "Payment processing and management APIs")
public class PaymentController {

    private final PaymentService paymentService;
    private final RefundService refundService;

    /**
     * Process a new payment
     */
    @PostMapping
    @Operation(summary = "Process payment", description = "Process a new payment transaction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Payment processing failed")
    })
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        log.info("POST /api/payments - Processing payment for order: {}", request.getOrderId());
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        log.info("GET /api/payments/{} - Fetching payment", paymentId);
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by payment number
     */
    @GetMapping("/number/{paymentNumber}")
    @Operation(summary = "Get payment by number", description = "Retrieve payment details by payment number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getPaymentByNumber(
            @Parameter(description = "Payment Number") @PathVariable String paymentNumber) {
        log.info("GET /api/payments/number/{} - Fetching payment", paymentNumber);
        PaymentResponse response = paymentService.getPaymentByNumber(paymentNumber);
        return ResponseEntity.ok(response);
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID", description = "Retrieve payment for a specific order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @Parameter(description = "Order ID") @PathVariable Long orderId) {
        log.info("GET /api/payments/order/{} - Fetching payment", orderId);
        PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer payments
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get customer payments", description = "Retrieve all payments for a customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully")
    })
    public ResponseEntity<Page<PaymentResponse>> getCustomerPayments(
            @Parameter(description = "Customer ID") @PathVariable Long customerId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("GET /api/payments/customer/{} - Fetching customer payments", customerId);
        Page<PaymentResponse> response = paymentService.getCustomerPayments(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel payment
     */
    @PostMapping("/{paymentId}/cancel")
    @Operation(summary = "Cancel payment", description = "Cancel a pending payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment cancelled successfully"),
        @ApiResponse(responseCode = "400", description = "Payment cannot be cancelled"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<PaymentResponse> cancelPayment(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        log.info("POST /api/payments/{}/cancel - Cancelling payment", paymentId);
        PaymentResponse response = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Create refund
     */
    @PostMapping("/{paymentId}/refunds")
    @Operation(summary = "Create refund", description = "Process a refund for a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refund request"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<RefundResponse> createRefund(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId,
            @Valid @RequestBody CreateRefundRequest request) {
        log.info("POST /api/payments/{}/refunds - Processing refund", paymentId);
        RefundResponse response = refundService.processRefund(paymentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get refunds for payment
     */
    @GetMapping("/{paymentId}/refunds")
    @Operation(summary = "Get payment refunds", description = "Retrieve all refunds for a payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refunds retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    public ResponseEntity<List<RefundResponse>> getPaymentRefunds(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        log.info("GET /api/payments/{}/refunds - Fetching refunds", paymentId);
        List<RefundResponse> response = refundService.getRefundsForPayment(paymentId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get refund by ID
     */
    @GetMapping("/refunds/{refundId}")
    @Operation(summary = "Get refund by ID", description = "Retrieve refund details by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Refund not found")
    })
    public ResponseEntity<RefundResponse> getRefundById(
            @Parameter(description = "Refund ID") @PathVariable Long refundId) {
        log.info("GET /api/payments/refunds/{} - Fetching refund", refundId);
        RefundResponse response = refundService.getRefundById(refundId);
        return ResponseEntity.ok(response);
    }
}
