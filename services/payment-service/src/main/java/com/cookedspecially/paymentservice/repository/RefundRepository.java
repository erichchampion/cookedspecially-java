package com.cookedspecially.paymentservice.repository;

import com.cookedspecially.paymentservice.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Refund Repository
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {

    /**
     * Find refund by refund number
     */
    Optional<Refund> findByRefundNumber(String refundNumber);

    /**
     * Find refunds by payment ID
     */
    List<Refund> findByPaymentId(Long paymentId);

    /**
     * Find refunds by order ID
     */
    List<Refund> findByOrderId(Long orderId);

    /**
     * Find refunds by status
     */
    List<Refund> findByStatus(Refund.RefundStatus status);

    /**
     * Find refund by external refund ID
     */
    Optional<Refund> findByExternalRefundId(String externalRefundId);
}
