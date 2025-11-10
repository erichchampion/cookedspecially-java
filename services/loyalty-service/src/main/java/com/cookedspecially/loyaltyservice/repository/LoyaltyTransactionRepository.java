package com.cookedspecially.loyaltyservice.repository;

import com.cookedspecially.loyaltyservice.domain.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {

    List<LoyaltyTransaction> findByCustomerIdOrderByTransactionDateDesc(Integer customerId);

    List<LoyaltyTransaction> findByRestaurantId(Integer restaurantId);

    List<LoyaltyTransaction> findByOrderId(Long orderId);

    @Query("SELECT SUM(lt.points) FROM LoyaltyTransaction lt WHERE lt.customerId = :customerId")
    Integer calculateTotalPointsByCustomerId(Integer customerId);
}
