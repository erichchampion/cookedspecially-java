package com.cookedspecially.paymentservice.repository;

import com.cookedspecially.paymentservice.domain.Payment;
import com.cookedspecially.paymentservice.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Payment Repository
 *
 * Data access layer for Payment entities
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by payment number
     */
    Optional<Payment> findByPaymentNumber(String paymentNumber);

    /**
     * Find payment by order ID
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Find payments by customer ID
     */
    Page<Payment> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find payments by status
     */
    List<Payment> findByStatus(PaymentStatus status);

    /**
     * Find payments by customer and status
     */
    List<Payment> findByCustomerIdAndStatus(Long customerId, PaymentStatus status);

    /**
     * Find payments created within date range
     */
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find failed payments
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' ORDER BY p.createdAt DESC")
    List<Payment> findFailedPayments();

    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffTime")
    List<Payment> findStalePendingPayments(@Param("cutoffTime") LocalDateTime cutoffTime);

    /**
     * Count payments by status
     */
    long countByStatus(PaymentStatus status);

    /**
     * Count payments by customer
     */
    long countByCustomerId(Long customerId);

    /**
     * Find payment by external transaction ID
     */
    Optional<Payment> findByExternalTransactionId(String externalTransactionId);
}
