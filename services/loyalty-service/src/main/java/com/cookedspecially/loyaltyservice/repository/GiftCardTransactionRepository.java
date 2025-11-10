package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.GiftCardTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiftCardTransactionRepository extends JpaRepository<GiftCardTransaction, Long> {

    List<GiftCardTransaction> findByGiftCardIdOrderByTransactionDateDesc(Long giftCardId);

    List<GiftCardTransaction> findByCustomerId(Integer customerId);

    List<GiftCardTransaction> findByOrderId(Long orderId);
}
