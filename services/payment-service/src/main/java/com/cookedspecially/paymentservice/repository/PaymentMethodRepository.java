package com.cookedspecially.paymentservice.repository;

import com.cookedspecially.paymentservice.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Payment Method Repository
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {

    /**
     * Find payment methods by customer ID
     */
    List<PaymentMethod> findByCustomerIdAndIsActiveTrue(Long customerId);

    /**
     * Find default payment method for customer
     */
    Optional<PaymentMethod> findByCustomerIdAndIsDefaultTrueAndIsActiveTrue(Long customerId);

    /**
     * Find payment method by external ID
     */
    Optional<PaymentMethod> findByExternalPaymentMethodId(String externalPaymentMethodId);

    /**
     * Find expired payment methods
     */
    @Query("SELECT pm FROM PaymentMethod pm WHERE " +
           "pm.isActive = true AND " +
           "CONCAT(pm.cardExpYear, pm.cardExpMonth) < :currentYearMonth")
    List<PaymentMethod> findExpiredPaymentMethods(@Param("currentYearMonth") String currentYearMonth);

    /**
     * Count active payment methods for customer
     */
    long countByCustomerIdAndIsActiveTrue(Long customerId);
}
