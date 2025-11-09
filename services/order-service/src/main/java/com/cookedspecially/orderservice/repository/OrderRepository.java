package com.cookedspecially.orderservice.repository;

import com.cookedspecially.orderservice.domain.Order;
import com.cookedspecially.orderservice.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Order Repository
 *
 * Data access layer for Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find order by order number
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by customer ID
     */
    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Find orders by restaurant ID
     */
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by customer and status
     */
    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);

    /**
     * Find orders by restaurant and status
     */
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);

    /**
     * Find orders created within a date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find active orders (not delivered or cancelled)
     */
    @Query("SELECT o FROM Order o WHERE o.status NOT IN ('DELIVERED', 'CANCELLED') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrders();

    /**
     * Find pending orders for a restaurant
     */
    @Query("SELECT o FROM Order o WHERE o.restaurantId = :restaurantId AND o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Order> findPendingOrdersForRestaurant(@Param("restaurantId") Long restaurantId);

    /**
     * Count orders by status
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders for customer
     */
    long countByCustomerId(Long customerId);
}
