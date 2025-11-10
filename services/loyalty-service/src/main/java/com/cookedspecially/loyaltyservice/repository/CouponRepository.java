package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.Coupon;
import com.cookedspecially.loyaltyservice.domain.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndRestaurantId(String code, Integer restaurantId);

    List<Coupon> findByRestaurantId(Integer restaurantId);

    List<Coupon> findByRestaurantIdAndStatus(Integer restaurantId, CouponStatus status);

    @Query("SELECT c FROM Coupon c WHERE c.restaurantId = :restaurantId AND " +
           "(c.status = 'ENABLED' OR c.status = 'DISABLED')")
    List<Coupon> findActiveAndDisabledByRestaurant(@Param("restaurantId") Integer restaurantId);

    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.restaurantId = :restaurantId AND c.status = 'ENABLED'")
    Optional<Coupon> findEnabledCouponByCodeAndRestaurant(@Param("code") String code, @Param("restaurantId") Integer restaurantId);

    boolean existsByCodeAndRestaurantId(String code, Integer restaurantId);
}
