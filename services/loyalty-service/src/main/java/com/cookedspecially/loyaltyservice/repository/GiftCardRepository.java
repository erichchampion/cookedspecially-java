package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.GiftCard;
import com.cookedspecially.loyaltyservice.domain.GiftCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    Optional<GiftCard> findByCardNumber(String cardNumber);

    List<GiftCard> findByRestaurantId(Integer restaurantId);

    List<GiftCard> findByRestaurantIdAndStatus(Integer restaurantId, GiftCardStatus status);

    List<GiftCard> findByPurchaserCustomerId(Integer customerId);

    List<GiftCard> findByRecipientEmail(String email);

    List<GiftCard> findByRecipientPhone(String phone);

    boolean existsByCardNumber(String cardNumber);
}
