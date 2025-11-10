package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.*;
import com.cookedspecially.kitchenservice.repository.TillHandoverRepository;
import com.cookedspecially.kitchenservice.repository.TillRepository;
import com.cookedspecially.kitchenservice.repository.TillTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Till/Cash Register operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TillServiceImpl implements TillService {

    private final TillRepository tillRepository;
    private final TillTransactionRepository transactionRepository;
    private final TillHandoverRepository handoverRepository;

    @Override
    public Till createTill(Till till) {
        log.info("Creating till: {}", till.getName());

        // Check if till with same name exists for restaurant
        if (tillRepository.existsByNameAndRestaurantId(till.getName(), till.getRestaurantId())) {
            throw new IllegalArgumentException("Till with name '" + till.getName() + "' already exists for this restaurant");
        }

        till.setStatus(TillStatus.CLOSED);
        till.setCurrentBalance(BigDecimal.ZERO);
        till.setExpectedBalance(BigDecimal.ZERO);

        return tillRepository.save(till);
    }

    @Override
    public Till updateTill(Long tillId, Till updatedTill) {
        log.info("Updating till: {}", tillId);

        Till till = getTillById(tillId);

        // Can only update certain fields when till is closed
        if (till.getStatus() == TillStatus.OPEN) {
            throw new IllegalStateException("Cannot update till while it is open");
        }

        till.setName(updatedTill.getName());
        till.setDescription(updatedTill.getDescription());
        till.setUpdatedBy(updatedTill.getUpdatedBy());

        return tillRepository.save(till);
    }

    @Override
    @Transactional(readOnly = true)
    public Till getTillById(Long tillId) {
        return tillRepository.findById(tillId)
            .orElseThrow(() -> new IllegalArgumentException("Till not found with ID: " + tillId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Till> getTillsByFulfillmentCenter(Long fulfillmentCenterId) {
        return tillRepository.findByFulfillmentCenterId(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Till> getTillsByRestaurant(Long restaurantId) {
        return tillRepository.findByRestaurantId(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Till> getTillsByStatus(TillStatus status) {
        return tillRepository.findByStatus(status);
    }

    @Override
    public Till openTill(Long tillId, BigDecimal openingBalance, String userId, String userName) {
        log.info("Opening till {} with opening balance: {}", tillId, openingBalance);

        Till till = getTillById(tillId);

        if (till.getStatus() == TillStatus.OPEN) {
            throw new IllegalStateException("Till is already open");
        }

        // Check if user has another till open
        tillRepository.findByCurrentUserIdAndStatus(userId, TillStatus.OPEN)
            .ifPresent(openTill -> {
                throw new IllegalStateException("User already has till " + openTill.getId() + " open");
            });

        till.open(openingBalance, userId, userName);
        till = tillRepository.save(till);

        // Record opening balance transaction
        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .transactionType(TransactionType.OPENING_BALANCE)
            .amount(openingBalance)
            .balanceAfter(openingBalance)
            .notes("Till opened")
            .performedBy(userId)
            .performedByName(userName)
            .build();

        transactionRepository.save(transaction);

        log.info("Till {} opened successfully by {}", tillId, userName);
        return till;
    }

    @Override
    public Till closeTill(Long tillId, BigDecimal closingBalance) {
        log.info("Closing till {} with closing balance: {}", tillId, closingBalance);

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        // Record closing balance transaction
        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .transactionType(TransactionType.CLOSING_BALANCE)
            .amount(closingBalance)
            .balanceAfter(closingBalance)
            .notes("Till closed. Variance: " + till.getVariance())
            .performedBy(till.getCurrentUserId())
            .performedByName(till.getCurrentUserName())
            .build();

        transactionRepository.save(transaction);

        // Update current balance to closing balance
        till.setCurrentBalance(closingBalance);
        till.close();

        log.info("Till {} closed successfully. Variance: {}", tillId, till.getVariance());
        return tillRepository.save(till);
    }

    @Override
    public TillTransaction addCash(Long tillId, BigDecimal amount, String notes,
                                   String performedBy, String performedByName) {
        log.info("Adding cash {} to till {}", amount, tillId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        till.addCash(amount);
        tillRepository.save(till);

        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .transactionType(TransactionType.CASH_IN)
            .amount(amount)
            .balanceAfter(till.getCurrentBalance())
            .notes(notes)
            .performedBy(performedBy)
            .performedByName(performedByName)
            .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public TillTransaction withdrawCash(Long tillId, BigDecimal amount, String notes,
                                       String performedBy, String performedByName) {
        log.info("Withdrawing cash {} from till {}", amount, tillId);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        if (till.getCurrentBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance in till");
        }

        till.withdrawCash(amount);
        tillRepository.save(till);

        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .transactionType(TransactionType.CASH_OUT)
            .amount(amount)
            .balanceAfter(till.getCurrentBalance())
            .notes(notes)
            .performedBy(performedBy)
            .performedByName(performedByName)
            .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public TillTransaction recordSale(Long tillId, Long orderId, BigDecimal amount, String paymentMethod,
                                     String performedBy, String performedByName) {
        log.info("Recording sale {} for order {} in till {}", amount, orderId, tillId);

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        // For cash payments, add to current balance
        if ("cash".equalsIgnoreCase(paymentMethod)) {
            till.addCash(amount);
        } else {
            // For non-cash, only update expected balance
            till.recordSale(amount);
        }

        tillRepository.save(till);

        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .orderId(orderId)
            .transactionType(TransactionType.SALE)
            .amount(amount)
            .balanceAfter(till.getCurrentBalance())
            .paymentMethod(paymentMethod)
            .reference("Order #" + orderId)
            .performedBy(performedBy)
            .performedByName(performedByName)
            .build();

        return transactionRepository.save(transaction);
    }

    @Override
    public TillTransaction recordRefund(Long tillId, Long orderId, BigDecimal amount, String notes,
                                       String performedBy, String performedByName) {
        log.info("Recording refund {} for order {} in till {}", amount, orderId, tillId);

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        till.withdrawCash(amount);
        tillRepository.save(till);

        TillTransaction transaction = TillTransaction.builder()
            .tillId(tillId)
            .orderId(orderId)
            .transactionType(TransactionType.REFUND)
            .amount(amount)
            .balanceAfter(till.getCurrentBalance())
            .notes(notes)
            .reference("Refund for Order #" + orderId)
            .performedBy(performedBy)
            .performedByName(performedByName)
            .build();

        return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTillBalance(Long tillId) {
        Till till = getTillById(tillId);
        return till.getCurrentBalance();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TillTransaction> getTillTransactions(Long tillId) {
        return transactionRepository.findByTillIdOrderByTransactionDateDesc(tillId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TillTransaction> getTillTransactionsByDateRange(Long tillId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByTillIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            tillId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalSales(Long tillId, LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = transactionRepository.calculateTotalSales(tillId, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getPaymentMethodSummary(Long tillId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.getPaymentMethodSummary(tillId, startDate, endDate);
    }

    @Override
    public TillHandover initiateHandover(Long tillId, String fromUserId, String fromUserName,
                                        String toUserId, String toUserName, BigDecimal actualBalance, String notes) {
        log.info("Initiating handover for till {} from {} to {}", tillId, fromUserName, toUserName);

        Till till = getTillById(tillId);

        if (till.getStatus() != TillStatus.OPEN) {
            throw new IllegalStateException("Till is not open");
        }

        // Check for pending handovers
        if (handoverRepository.countByTillIdAndStatus(tillId, HandoverStatus.PENDING) > 0) {
            throw new IllegalStateException("There is already a pending handover for this till");
        }

        BigDecimal variance = actualBalance.subtract(till.getExpectedBalance());

        TillHandover handover = TillHandover.builder()
            .tillId(tillId)
            .fromUserId(fromUserId)
            .fromUserName(fromUserName)
            .toUserId(toUserId)
            .toUserName(toUserName)
            .expectedBalance(till.getExpectedBalance())
            .actualBalance(actualBalance)
            .variance(variance)
            .status(HandoverStatus.PENDING)
            .notes(notes)
            .build();

        return handoverRepository.save(handover);
    }

    @Override
    public TillHandover approveHandover(Long handoverId, String approverId) {
        log.info("Approving handover {}", handoverId);

        TillHandover handover = handoverRepository.findById(handoverId)
            .orElseThrow(() -> new IllegalArgumentException("Handover not found with ID: " + handoverId));

        if (handover.getStatus() != HandoverStatus.PENDING) {
            throw new IllegalStateException("Handover is not in pending state");
        }

        handover.approve(approverId);
        handover.complete();

        // Update till with new user
        Till till = getTillById(handover.getTillId());
        till.setCurrentUserId(handover.getToUserId());
        till.setCurrentUserName(handover.getToUserName());
        till.setCurrentBalance(handover.getActualBalance());
        tillRepository.save(till);

        return handoverRepository.save(handover);
    }

    @Override
    public TillHandover rejectHandover(Long handoverId, String reason) {
        log.info("Rejecting handover {}: {}", handoverId, reason);

        TillHandover handover = handoverRepository.findById(handoverId)
            .orElseThrow(() -> new IllegalArgumentException("Handover not found with ID: " + handoverId));

        if (handover.getStatus() != HandoverStatus.PENDING) {
            throw new IllegalStateException("Handover is not in pending state");
        }

        handover.reject(reason);
        return handoverRepository.save(handover);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TillHandover> getHandoverHistory(Long tillId) {
        return handoverRepository.findByTillIdOrderByHandoverDateDesc(tillId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TillHandover> getPendingHandovers() {
        return handoverRepository.findByStatus(HandoverStatus.PENDING);
    }

    @Override
    public void deleteTill(Long tillId) {
        log.info("Deleting till {}", tillId);

        Till till = getTillById(tillId);

        if (till.getStatus() == TillStatus.OPEN) {
            throw new IllegalStateException("Cannot delete an open till");
        }

        tillRepository.delete(till);
    }
}
