package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.Promotion;
import com.cookedspecially.loyaltyservice.domain.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    List<Promotion> findByRestaurantId(Integer restaurantId);

    List<Promotion> findByRestaurantIdAndStatus(Integer restaurantId, PromotionStatus status);

    List<Promotion> findByCouponId(Long couponId);
}
