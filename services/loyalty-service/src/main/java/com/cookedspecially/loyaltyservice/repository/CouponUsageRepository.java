package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    List<CouponUsage> findByCouponId(Long couponId);

    List<CouponUsage> findByCustomerId(Integer customerId);

    boolean existsByCouponIdAndCustomerId(Long couponId, Integer customerId);

    long countByCouponId(Long couponId);
}
