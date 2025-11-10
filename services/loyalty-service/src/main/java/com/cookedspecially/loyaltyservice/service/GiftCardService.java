package com.cookedspecially.loyaltyservice.service;

import com.cookedspecially.loyaltyservice.domain.GiftCard;
import com.cookedspecially.loyaltyservice.domain.GiftCardStatus;
import com.cookedspecially.loyaltyservice.domain.GiftCardTransaction;
import com.cookedspecially.loyaltyservice.domain.TransactionType;
import com.cookedspecially.loyaltyservice.dto.CreateGiftCardRequest;
import com.cookedspecially.loyaltyservice.dto.GiftCardResponse;
import com.cookedspecially.loyaltyservice.dto.RedeemGiftCardRequest;
import com.cookedspecially.loyaltyservice.exception.GiftCardNotFoundException;
import com.cookedspecially.loyaltyservice.exception.InvalidGiftCardException;
import com.cookedspecially.loyaltyservice.repository.GiftCardRepository;
import com.cookedspecially.loyaltyservice.repository.GiftCardTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GiftCardService {

    @Autowired
    private GiftCardRepository giftCardRepository;

    @Autowired
    private GiftCardTransactionRepository transactionRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    public List<GiftCardResponse> createGiftCards(CreateGiftCardRequest request) {
        List<GiftCardResponse> createdCards = new ArrayList<>();

        for (int i = 0; i < request.getCount(); i++) {
            GiftCard giftCard = new GiftCard();
            giftCard.setCardNumber(generateCardNumber());
            giftCard.setRestaurantId(request.getRestaurantId());
            giftCard.setInitialAmount(request.getAmount());
            giftCard.setCurrentBalance(request.getAmount());
            giftCard.setCategory(request.getCategory());
            giftCard.setRecipientName(request.getRecipientName());
            giftCard.setRecipientPhone(request.getRecipientPhone());
            giftCard.setRecipientEmail(request.getRecipientEmail());
            giftCard.setMessage(request.getMessage());
            giftCard.setStatus(GiftCardStatus.CREATED);

            if (request.getExpiryDays() != null) {
                giftCard.setExpiresAt(LocalDateTime.now().plusDays(request.getExpiryDays()));
            }

            giftCard = giftCardRepository.save(giftCard);
            createdCards.add(new GiftCardResponse(giftCard));
        }

        return createdCards;
    }

    public GiftCardResponse activateGiftCard(String cardNumber, Integer customerId, String invoiceId) {
        GiftCard giftCard = giftCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found"));

        if (giftCard.getStatus() != GiftCardStatus.CREATED) {
            throw new InvalidGiftCardException("Gift card is not in CREATED status");
        }

        giftCard.setStatus(GiftCardStatus.ACTIVE);
        giftCard.setActivatedAt(LocalDateTime.now());
        giftCard.setPurchaserCustomerId(customerId);
        giftCard.setInvoiceId(invoiceId);
        giftCard.setPurchasedAt(LocalDateTime.now());

        giftCard = giftCardRepository.save(giftCard);

        // Record transaction
        recordTransaction(giftCard.getId(), TransactionType.ACTIVATE, giftCard.getInitialAmount(),
                giftCard.getCurrentBalance(), customerId, null, "Gift card activated");

        return new GiftCardResponse(giftCard);
    }

    public GiftCardResponse getGiftCard(String cardNumber) {
        GiftCard giftCard = giftCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found"));
        return new GiftCardResponse(giftCard);
    }

    public List<GiftCardResponse> getGiftCardsByRestaurant(Integer restaurantId, GiftCardStatus status) {
        List<GiftCard> giftCards = status == null
                ? giftCardRepository.findByRestaurantId(restaurantId)
                : giftCardRepository.findByRestaurantIdAndStatus(restaurantId, status);
        return giftCards.stream().map(GiftCardResponse::new).collect(Collectors.toList());
    }

    public List<GiftCardResponse> getGiftCardsByCustomer(Integer customerId) {
        List<GiftCard> giftCards = giftCardRepository.findByPurchaserCustomerId(customerId);
        return giftCards.stream().map(GiftCardResponse::new).collect(Collectors.toList());
    }

    public GiftCardResponse redeemGiftCard(RedeemGiftCardRequest request) {
        GiftCard giftCard = giftCardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found"));

        if (!giftCard.isActive()) {
            throw new InvalidGiftCardException("Gift card is not active");
        }

        if (!giftCard.canRedeem(request.getAmount())) {
            throw new InvalidGiftCardException("Insufficient gift card balance");
        }

        BigDecimal balanceBefore = giftCard.getCurrentBalance();
        giftCard.deductBalance(request.getAmount());
        giftCard = giftCardRepository.save(giftCard);

        // Record transaction
        recordTransaction(giftCard.getId(), TransactionType.USE, request.getAmount(),
                giftCard.getCurrentBalance(), request.getCustomerId(), request.getOrderId(),
                "Gift card redeemed");

        return new GiftCardResponse(giftCard);
    }

    public GiftCardResponse deactivateGiftCard(String cardNumber) {
        GiftCard giftCard = giftCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found"));

        if (giftCard.getStatus() == GiftCardStatus.REDEEMED) {
            throw new InvalidGiftCardException("Cannot deactivate a fully redeemed gift card");
        }

        giftCard.setStatus(GiftCardStatus.DEACTIVATED);
        giftCard = giftCardRepository.save(giftCard);

        return new GiftCardResponse(giftCard);
    }

    public GiftCardResponse reactivateGiftCard(String cardNumber) {
        GiftCard giftCard = giftCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new GiftCardNotFoundException("Gift card not found"));

        if (giftCard.getStatus() != GiftCardStatus.DEACTIVATED) {
            throw new InvalidGiftCardException("Only deactivated gift cards can be reactivated");
        }

        giftCard.setStatus(GiftCardStatus.ACTIVE);
        giftCard = giftCardRepository.save(giftCard);

        return new GiftCardResponse(giftCard);
    }

    private void recordTransaction(Long giftCardId, TransactionType type, BigDecimal amount,
                                    BigDecimal balanceAfter, Integer customerId, Long orderId, String notes) {
        GiftCardTransaction transaction = new GiftCardTransaction();
        transaction.setGiftCardId(giftCardId);
        transaction.setTransactionType(type);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setCustomerId(customerId);
        transaction.setOrderId(orderId);
        transaction.setNotes(notes);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    private String generateCardNumber() {
        String cardNumber;
        do {
            // Generate 16-digit card number
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(RANDOM.nextInt(10));
            }
            cardNumber = sb.toString();
        } while (giftCardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }
}
