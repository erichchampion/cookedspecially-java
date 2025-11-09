package com.cookedspecially.restaurantservice.repository;

import com.cookedspecially.restaurantservice.domain.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Menu Item Repository
 */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find menu items by restaurant ID
     */
    List<MenuItem> findByRestaurantIdOrderByDisplayOrderAsc(Long restaurantId);

    /**
     * Find menu items by restaurant ID with pagination
     */
    Page<MenuItem> findByRestaurantId(Long restaurantId, Pageable pageable);

    /**
     * Find available menu items by restaurant ID
     */
    List<MenuItem> findByRestaurantIdAndIsAvailableTrueOrderByDisplayOrderAsc(Long restaurantId);

    /**
     * Find available menu items by restaurant ID with pagination
     */
    Page<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId, Pageable pageable);

    /**
     * Find menu items by category
     */
    List<MenuItem> findByRestaurantIdAndCategoryOrderByDisplayOrderAsc(
        Long restaurantId, String category);

    /**
     * Find menu items by category with pagination
     */
    Page<MenuItem> findByRestaurantIdAndCategoryAndIsAvailableTrue(
        Long restaurantId, String category, Pageable pageable);

    /**
     * Find vegetarian menu items
     */
    List<MenuItem> findByRestaurantIdAndIsVegetarianTrueAndIsAvailableTrue(Long restaurantId);

    /**
     * Find vegetarian menu items with pagination
     */
    Page<MenuItem> findByRestaurantIdAndIsVegetarianTrueAndIsAvailableTrue(
        Long restaurantId, Pageable pageable);

    /**
     * Find vegan menu items
     */
    List<MenuItem> findByRestaurantIdAndIsVeganTrueAndIsAvailableTrue(Long restaurantId);

    /**
     * Find vegan menu items with pagination
     */
    Page<MenuItem> findByRestaurantIdAndIsVeganTrueAndIsAvailableTrue(
        Long restaurantId, Pageable pageable);

    /**
     * Find gluten-free menu items with pagination
     */
    Page<MenuItem> findByRestaurantIdAndIsGlutenFreeTrueAndIsAvailableTrue(
        Long restaurantId, Pageable pageable);

    /**
     * Search menu items by name
     */
    @Query("SELECT m FROM MenuItem m WHERE " +
           "m.restaurant.id = :restaurantId AND " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "m.isAvailable = true")
    List<MenuItem> searchByName(
        @Param("restaurantId") Long restaurantId,
        @Param("searchTerm") String searchTerm);

    /**
     * Search menu items by name with pagination
     */
    @Query("SELECT m FROM MenuItem m WHERE " +
           "m.restaurant.id = :restaurantId AND " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND " +
           "m.isAvailable = true")
    Page<MenuItem> searchByName(
        @Param("restaurantId") Long restaurantId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    /**
     * Count available items for restaurant
     */
    long countByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    /**
     * Get distinct categories for restaurant
     */
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurant.id = :restaurantId")
    List<String> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);
}
