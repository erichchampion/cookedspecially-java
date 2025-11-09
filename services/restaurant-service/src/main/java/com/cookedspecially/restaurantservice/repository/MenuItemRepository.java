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
     * Find available menu items by restaurant ID
     */
    List<MenuItem> findByRestaurantIdAndIsAvailableTrueOrderByDisplayOrderAsc(Long restaurantId);

    /**
     * Find menu items by category
     */
    List<MenuItem> findByRestaurantIdAndCategoryOrderByDisplayOrderAsc(
        Long restaurantId, String category);

    /**
     * Find vegetarian menu items
     */
    List<MenuItem> findByRestaurantIdAndIsVegetarianTrueAndIsAvailableTrue(Long restaurantId);

    /**
     * Find vegan menu items
     */
    List<MenuItem> findByRestaurantIdAndIsVeganTrueAndIsAvailableTrue(Long restaurantId);

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
     * Count available items for restaurant
     */
    long countByRestaurantIdAndIsAvailableTrue(Long restaurantId);

    /**
     * Get distinct categories for restaurant
     */
    @Query("SELECT DISTINCT m.category FROM MenuItem m WHERE m.restaurant.id = :restaurantId")
    List<String> findDistinctCategoriesByRestaurantId(@Param("restaurantId") Long restaurantId);
}
