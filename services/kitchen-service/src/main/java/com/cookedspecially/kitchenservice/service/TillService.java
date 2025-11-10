package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.Till;
import com.cookedspecially.kitchenservice.domain.TillHandover;
import com.cookedspecially.kitchenservice.domain.TillStatus;
import com.cookedspecially.kitchenservice.domain.TillTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Till/Cash Register operations
 */
public interface TillService {

    /**
     * Create a new till
     */
    Till createTill(Till till);

    /**
     * Update till information
     */
    Till updateTill(Long tillId, Till till);

    /**
     * Get till by ID
     */
    Till getTillById(Long tillId);

    /**
     * Get all tills for a fulfillment center
     */
    List<Till> getTillsByFulfillmentCenter(Long fulfillmentCenterId);

    /**
     * Get all tills for a restaurant
     */
    List<Till> getTillsByRestaurant(Long restaurantId);

    /**
     * Get tills by status
     */
    List<Till> getTillsByStatus(TillStatus status);

    /**
     * Open till for shift
     */
    Till openTill(Long tillId, BigDecimal openingBalance, String userId, String userName);

    /**
     * Close till for shift
     */
    Till closeTill(Long tillId, BigDecimal closingBalance);

    /**
     * Add cash to till
     */
    TillTransaction addCash(Long tillId, BigDecimal amount, String notes, String performedBy, String performedByName);

    /**
     * Withdraw cash from till
     */
    TillTransaction withdrawCash(Long tillId, BigDecimal amount, String notes, String performedBy, String performedByName);

    /**
     * Record a sale transaction
     */
    TillTransaction recordSale(Long tillId, Long orderId, BigDecimal amount, String paymentMethod,
                               String performedBy, String performedByName);

    /**
     * Record a refund transaction
     */
    TillTransaction recordRefund(Long tillId, Long orderId, BigDecimal amount, String notes,
                                 String performedBy, String performedByName);

    /**
     * Get till balance
     */
    BigDecimal getTillBalance(Long tillId);

    /**
     * Get till transactions
     */
    List<TillTransaction> getTillTransactions(Long tillId);

    /**
     * Get till transactions by date range
     */
    List<TillTransaction> getTillTransactionsByDateRange(Long tillId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculate total sales for till
     */
    BigDecimal calculateTotalSales(Long tillId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get payment method summary
     */
    List<Object[]> getPaymentMethodSummary(Long tillId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Initiate till handover
     */
    TillHandover initiateHandover(Long tillId, String fromUserId, String fromUserName,
                                  String toUserId, String toUserName, BigDecimal actualBalance, String notes);

    /**
     * Approve handover
     */
    TillHandover approveHandover(Long handoverId, String approverId);

    /**
     * Reject handover
     */
    TillHandover rejectHandover(Long handoverId, String reason);

    /**
     * Get handover history for till
     */
    List<TillHandover> getHandoverHistory(Long tillId);

    /**
     * Get pending handovers
     */
    List<TillHandover> getPendingHandovers();

    /**
     * Delete till
     */
    void deleteTill(Long tillId);
}
