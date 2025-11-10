package com.cookedspecially.loyaltyservice.controller;

import com.cookedspecially.loyaltyservice.domain.GiftCardStatus;
import com.cookedspecially.loyaltyservice.dto.CreateGiftCardRequest;
import com.cookedspecially.loyaltyservice.dto.GiftCardResponse;
import com.cookedspecially.loyaltyservice.dto.RedeemGiftCardRequest;
import com.cookedspecially.loyaltyservice.service.GiftCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/gift-cards")
@Tag(name = "Gift Card Management", description = "APIs for managing gift cards")
public class GiftCardController {

    @Autowired
    private GiftCardService giftCardService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Create new gift cards (can create multiple)")
    public ResponseEntity<List<GiftCardResponse>> createGiftCards(@Valid @RequestBody CreateGiftCardRequest request) {
        List<GiftCardResponse> response = giftCardService.createGiftCards(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/{cardNumber}/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER', 'ROLE_SYSTEM')")
    @Operation(summary = "Activate a gift card")
    public ResponseEntity<GiftCardResponse> activateGiftCard(@PathVariable String cardNumber,
                                                              @RequestBody Map<String, Object> request) {
        Integer customerId = (Integer) request.get("customerId");
        String invoiceId = (String) request.get("invoiceId");
        GiftCardResponse response = giftCardService.activateGiftCard(cardNumber, customerId, invoiceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cardNumber}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER', 'ROLE_CUSTOMER', 'ROLE_SYSTEM')")
    @Operation(summary = "Get gift card details")
    public ResponseEntity<GiftCardResponse> getGiftCard(@PathVariable String cardNumber) {
        GiftCardResponse response = giftCardService.getGiftCard(cardNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Get all gift cards for a restaurant")
    public ResponseEntity<List<GiftCardResponse>> getGiftCardsByRestaurant(
            @PathVariable Integer restaurantId,
            @RequestParam(required = false) GiftCardStatus status) {
        List<GiftCardResponse> response = giftCardService.getGiftCardsByRestaurant(restaurantId, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_CUSTOMER', 'ROLE_SYSTEM')")
    @Operation(summary = "Get all gift cards purchased by a customer")
    public ResponseEntity<List<GiftCardResponse>> getGiftCardsByCustomer(@PathVariable Integer customerId) {
        List<GiftCardResponse> response = giftCardService.getGiftCardsByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_SYSTEM')")
    @Operation(summary = "Redeem gift card for payment")
    public ResponseEntity<GiftCardResponse> redeemGiftCard(@Valid @RequestBody RedeemGiftCardRequest request) {
        GiftCardResponse response = giftCardService.redeemGiftCard(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardNumber}/deactivate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Deactivate a gift card")
    public ResponseEntity<GiftCardResponse> deactivateGiftCard(@PathVariable String cardNumber) {
        GiftCardResponse response = giftCardService.deactivateGiftCard(cardNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{cardNumber}/reactivate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Reactivate a deactivated gift card")
    public ResponseEntity<GiftCardResponse> reactivateGiftCard(@PathVariable String cardNumber) {
        GiftCardResponse response = giftCardService.reactivateGiftCard(cardNumber);
        return ResponseEntity.ok(response);
    }
}
