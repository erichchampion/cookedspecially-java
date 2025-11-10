package com.cookedspecially.kitchenservice.repository;

import com.cookedspecially.kitchenservice.domain.TillTransaction;
import com.cookedspecially.kitchenservice.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Till Transaction entity
 */
@Repository
public interface TillTransactionRepository extends JpaRepository<TillTransaction, Long> {

    /**
     * Find transactions by till
     */
    List<TillTransaction> findByTillIdOrderByTransactionDateDesc(Long tillId);

    /**
     * Find transactions by till and date range
     */
    List<TillTransaction> findByTillIdAndTransactionDateBetweenOrderByTransactionDateDesc(
        Long tillId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions by type
     */
    List<TillTransaction> findByTillIdAndTransactionType(Long tillId, TransactionType transactionType);

    /**
     * Find transaction by order ID
     */
    List<TillTransaction> findByOrderId(Long orderId);

    /**
     * Calculate total sales for till in date range
     */
    @Query("SELECT SUM(t.amount) FROM TillTransaction t WHERE t.tillId = :tillId " +
           "AND t.transactionType = 'SALE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalSales(@Param("tillId") Long tillId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total by transaction type
     */
    @Query("SELECT SUM(t.amount) FROM TillTransaction t WHERE t.tillId = :tillId " +
           "AND t.transactionType = :type " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalByType(@Param("tillId") Long tillId,
                                    @Param("type") TransactionType type,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    /**
     * Count transactions by till and type
     */
    long countByTillIdAndTransactionType(Long tillId, TransactionType transactionType);

    /**
     * Find transactions by performer
     */
    List<TillTransaction> findByPerformedByOrderByTransactionDateDesc(String performedBy);

    /**
     * Get transaction summary by payment method
     */
    @Query("SELECT t.paymentMethod, SUM(t.amount) FROM TillTransaction t " +
           "WHERE t.tillId = :tillId AND t.transactionType = 'SALE' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.paymentMethod")
    List<Object[]> getPaymentMethodSummary(@Param("tillId") Long tillId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
