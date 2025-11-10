package com.cookedspecially.loyaltyservice.controller;

import com.cookedspecially.loyaltyservice.domain.CouponStatus;
import com.cookedspecially.loyaltyservice.dto.*;
import com.cookedspecially.loyaltyservice.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupon Management", description = "APIs for managing discount coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Create a new coupon")
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        CouponResponse response = couponService.createCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Update an existing coupon")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id,
                                                        @Valid @RequestBody CreateCouponRequest request) {
        CouponResponse response = couponService.updateCoupon(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER', 'ROLE_SYSTEM')")
    @Operation(summary = "Get coupon by ID")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long id) {
        CouponResponse response = couponService.getCoupon(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER', 'ROLE_SYSTEM')")
    @Operation(summary = "Get all coupons for a restaurant")
    public ResponseEntity<List<CouponResponse>> getCouponsByRestaurant(
            @PathVariable Integer restaurantId,
            @RequestParam(required = false) CouponStatus status) {
        List<CouponResponse> response = couponService.getCouponsByRestaurant(restaurantId, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    @Operation(summary = "Delete a coupon")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_SYSTEM')")
    @Operation(summary = "Validate a coupon for an order")
    public ResponseEntity<CouponValidationResponse> validateCoupon(@Valid @RequestBody ValidateCouponRequest request) {
        CouponValidationResponse response = couponService.validateCoupon(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/use")
    @PreAuthorize("hasAuthority('ROLE_SYSTEM')")
    @Operation(summary = "Record coupon usage (internal use only)")
    public ResponseEntity<Void> recordCouponUsage(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer customerId = (Integer) request.get("customerId");
        Long orderId = ((Number) request.get("orderId")).longValue();
        BigDecimal orderAmount = new BigDecimal(request.get("orderAmount").toString());
        BigDecimal discountAmount = new BigDecimal(request.get("discountAmount").toString());

        couponService.recordCouponUsage(id, customerId, orderId, orderAmount, discountAmount);
        return ResponseEntity.ok().build();
    }
}
