package com.cookedspecially.restaurantservice.repository;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Restaurant Repository
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Find restaurants by owner ID
     */
    List<Restaurant> findByOwnerId(Long ownerId);

    /**
     * Find restaurants by status
     */
    Page<Restaurant> findByStatus(RestaurantStatus status, Pageable pageable);

    /**
     * Find active restaurants
     */
    Page<Restaurant> findByStatusAndIsActiveTrue(RestaurantStatus status, Pageable pageable);

    /**
     * Find restaurants by cuisine type
     */
    Page<Restaurant> findByCuisineTypeAndStatusAndIsActiveTrue(
        CuisineType cuisineType, RestaurantStatus status, Pageable pageable);

    /**
     * Find restaurants by city
     */
    Page<Restaurant> findByCityAndStatusAndIsActiveTrue(
        String city, RestaurantStatus status, Pageable pageable);

    /**
     * Search restaurants by name
     */
    @Query("SELECT r FROM Restaurant r WHERE " +
           "LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "r.status = 'ACTIVE' AND r.isActive = true")
    Page<Restaurant> searchByName(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find restaurants near location (simplified - would use spatial queries in production)
     */
    @Query("SELECT r FROM Restaurant r WHERE " +
           "r.status = 'ACTIVE' AND r.isActive = true AND " +
           "r.latitude BETWEEN :minLat AND :maxLat AND " +
           "r.longitude BETWEEN :minLon AND :maxLon")
    List<Restaurant> findNearLocation(
        @Param("minLat") BigDecimal minLat,
        @Param("maxLat") BigDecimal maxLat,
        @Param("minLon") BigDecimal minLon,
        @Param("maxLon") BigDecimal maxLon);

    /**
     * Find top-rated restaurants
     */
    Page<Restaurant> findByStatusAndIsActiveTrueOrderByRatingDesc(
        RestaurantStatus status, Pageable pageable);

    /**
     * Count restaurants by status
     */
    long countByStatus(RestaurantStatus status);

    /**
     * Count active restaurants by owner
     */
    long countByOwnerIdAndStatus(Long ownerId, RestaurantStatus status);
}
