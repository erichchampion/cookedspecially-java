package com.cookedspecially.loyaltyservice.service;

import com.cookedspecially.loyaltyservice.domain.Coupon;
import com.cookedspecially.loyaltyservice.domain.CouponStatus;
import com.cookedspecially.loyaltyservice.domain.CouponUsage;
import com.cookedspecially.loyaltyservice.dto.*;
import com.cookedspecially.loyaltyservice.exception.CouponNotFoundException;
import com.cookedspecially.loyaltyservice.exception.InvalidCouponException;
import com.cookedspecially.loyaltyservice.repository.CouponRepository;
import com.cookedspecially.loyaltyservice.repository.CouponUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponUsageRepository couponUsageRepository;

    public CouponResponse createCoupon(CreateCouponRequest request) {
        // Check if coupon code already exists
        if (couponRepository.existsByCodeAndRestaurantId(request.getCode().toUpperCase(), request.getRestaurantId())) {
            throw new InvalidCouponException("Coupon code already exists for this restaurant");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode().toUpperCase());
        coupon.setName(request.getName());
        coupon.setDescription(request.getDescription());
        coupon.setRestaurantId(request.getRestaurantId());
        coupon.setStatus(request.getStatus());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderAmount(request.getMinOrderAmount());
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setMaxTotalUsage(request.getMaxTotalUsage());
        coupon.setOneTimePerCustomer(request.getOneTimePerCustomer());
        coupon.setApplicableOrderSource(request.getApplicableOrderSource());
        coupon.setApplicablePaymentMode(request.getApplicablePaymentMode());

        coupon = couponRepository.save(coupon);
        return new CouponResponse(coupon);
    }

    public CouponResponse updateCoupon(Long id, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));

        // If coupon has been used, don't allow certain modifications
        long usageCount = couponUsageRepository.countByCouponId(id);
        if (usageCount > 0) {
            // Only allow status and end date changes
            coupon.setStatus(request.getStatus());
            coupon.setEndDate(request.getEndDate());
        } else {
            // Full update allowed
            coupon.setName(request.getName());
            coupon.setDescription(request.getDescription());
            coupon.setStatus(request.getStatus());
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setMinOrderAmount(request.getMinOrderAmount());
            coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
            coupon.setStartDate(request.getStartDate());
            coupon.setEndDate(request.getEndDate());
            coupon.setMaxTotalUsage(request.getMaxTotalUsage());
            coupon.setOneTimePerCustomer(request.getOneTimePerCustomer());
            coupon.setApplicableOrderSource(request.getApplicableOrderSource());
            coupon.setApplicablePaymentMode(request.getApplicablePaymentMode());
        }

        coupon = couponRepository.save(coupon);
        return new CouponResponse(coupon);
    }

    public CouponResponse getCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));
        return new CouponResponse(coupon);
    }

    public List<CouponResponse> getCouponsByRestaurant(Integer restaurantId, CouponStatus status) {
        List<Coupon> coupons = status == null
                ? couponRepository.findByRestaurantId(restaurantId)
                : couponRepository.findByRestaurantIdAndStatus(restaurantId, status);
        return coupons.stream().map(CouponResponse::new).collect(Collectors.toList());
    }

    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found with id: " + id));

        long usageCount = couponUsageRepository.countByCouponId(id);
        if (usageCount > 0) {
            // Don't delete, just disable
            coupon.setStatus(CouponStatus.DISABLED);
            couponRepository.save(coupon);
        } else {
            couponRepository.delete(coupon);
        }
    }

    public CouponValidationResponse validateCoupon(ValidateCouponRequest request) {
        // Find enabled coupon
        Coupon coupon = couponRepository.findEnabledCouponByCodeAndRestaurant(
                request.getCouponCode().toUpperCase(), request.getRestaurantId())
                .orElse(null);

        if (coupon == null) {
            return new CouponValidationResponse(false, "Coupon not found or not active");
        }

        // Check if coupon is active (date range, usage count)
        if (!coupon.isActive()) {
            String message = "Coupon is not valid";
            if (coupon.getStartDate() != null && LocalDateTime.now().isBefore(coupon.getStartDate())) {
                message = "Coupon is not yet valid";
            } else if (coupon.getEndDate() != null && LocalDateTime.now().isAfter(coupon.getEndDate())) {
                message = "Coupon has expired";
            } else if (coupon.getMaxTotalUsage() != null && coupon.getCurrentUsageCount() >= coupon.getMaxTotalUsage()) {
                message = "Coupon usage limit reached";
            }
            return new CouponValidationResponse(false, message);
        }

        // Check one-time-per-customer rule
        if (coupon.getOneTimePerCustomer()) {
            boolean alreadyUsed = couponUsageRepository.existsByCouponIdAndCustomerId(coupon.getId(), request.getCustomerId());
            if (alreadyUsed) {
                return new CouponValidationResponse(false, "Coupon already used by this customer");
            }
        }

        // Check minimum order amount
        if (coupon.getMinOrderAmount() != null && request.getOrderAmount().compareTo(coupon.getMinOrderAmount()) < 0) {
            return new CouponValidationResponse(false,
                    String.format("Minimum order amount of %.2f required", coupon.getMinOrderAmount()));
        }

        // Check order source restriction
        if (coupon.getApplicableOrderSource() != null && !coupon.getApplicableOrderSource().equals("ANY")
                && request.getOrderSource() != null && !request.getOrderSource().equals(coupon.getApplicableOrderSource())) {
            return new CouponValidationResponse(false, "Coupon not applicable for this order source");
        }

        // Check payment mode restriction
        if (coupon.getApplicablePaymentMode() != null && !coupon.getApplicablePaymentMode().equals("ANY")
                && request.getPaymentMode() != null && !request.getPaymentMode().equals(coupon.getApplicablePaymentMode())) {
            return new CouponValidationResponse(false, "Coupon not applicable for this payment mode");
        }

        // Calculate discount
        BigDecimal discountAmount = coupon.calculateDiscount(request.getOrderAmount());

        return new CouponValidationResponse(true, "Coupon is valid", discountAmount, new CouponResponse(coupon));
    }

    public void recordCouponUsage(Long couponId, Integer customerId, Long orderId, BigDecimal orderAmount, BigDecimal discountAmount) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found"));

        coupon.incrementUsage();
        couponRepository.save(coupon);

        CouponUsage usage = new CouponUsage();
        usage.setCouponId(couponId);
        usage.setCustomerId(customerId);
        usage.setOrderId(orderId);
        usage.setOrderAmount(orderAmount);
        usage.setDiscountAmount(discountAmount);
        usage.setUsedAt(LocalDateTime.now());

        couponUsageRepository.save(usage);
    }
}
