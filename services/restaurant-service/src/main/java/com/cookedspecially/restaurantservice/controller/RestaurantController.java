package com.cookedspecially.restaurantservice.controller;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import com.cookedspecially.restaurantservice.dto.CreateRestaurantRequest;
import com.cookedspecially.restaurantservice.dto.RestaurantResponse;
import com.cookedspecially.restaurantservice.dto.UpdateRestaurantRequest;
import com.cookedspecially.restaurantservice.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Restaurant Controller
 */
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Restaurant", description = "Restaurant management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Create restaurant
     */
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Create new restaurant", description = "Create a new restaurant (restaurant owners only)")
    public ResponseEntity<RestaurantResponse> createRestaurant(
        @Valid @RequestBody CreateRestaurantRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Creating restaurant for user: {}", userId);

        RestaurantResponse response = restaurantService.createRestaurant(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get restaurant by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieve restaurant details by ID")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        log.info("Fetching restaurant: {}", id);

        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update restaurant
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Update restaurant", description = "Update restaurant details (owner only)")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
        @PathVariable Long id,
        @Valid @RequestBody UpdateRestaurantRequest request,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Updating restaurant {} by user: {}", id, userId);

        RestaurantResponse response = restaurantService.updateRestaurant(id, request, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update restaurant status (admin only)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update restaurant status", description = "Update restaurant status (admin only)")
    public ResponseEntity<RestaurantResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam RestaurantStatus status
    ) {
        log.info("Updating restaurant {} status to: {}", id, status);

        RestaurantResponse response = restaurantService.updateRestaurantStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Approve restaurant (admin only)
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve restaurant", description = "Approve pending restaurant (admin only)")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable Long id) {
        log.info("Approving restaurant: {}", id);

        RestaurantResponse response = restaurantService.approveRestaurant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Suspend restaurant (admin only)
     */
    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend restaurant", description = "Suspend restaurant (admin only)")
    public ResponseEntity<RestaurantResponse> suspendRestaurant(@PathVariable Long id) {
        log.info("Suspending restaurant: {}", id);

        RestaurantResponse response = restaurantService.suspendRestaurant(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Close restaurant temporarily
     */
    @PostMapping("/{id}/close")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Close restaurant temporarily", description = "Temporarily close restaurant (owner only)")
    public ResponseEntity<RestaurantResponse> closeRestaurant(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Closing restaurant {} by user: {}", id, userId);

        RestaurantResponse response = restaurantService.closeTemporarily(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reopen restaurant
     */
    @PostMapping("/{id}/reopen")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('ADMIN')")
    @Operation(summary = "Reopen restaurant", description = "Reopen temporarily closed restaurant (owner only)")
    public ResponseEntity<RestaurantResponse> reopenRestaurant(
        @PathVariable Long id,
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Reopening restaurant {} by user: {}", id, userId);

        RestaurantResponse response = restaurantService.reopenRestaurant(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get my restaurants
     */
    @GetMapping("/my-restaurants")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Get my restaurants", description = "Get all restaurants owned by current user")
    public ResponseEntity<List<RestaurantResponse>> getMyRestaurants(@AuthenticationPrincipal Jwt jwt) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching restaurants for user: {}", userId);

        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByOwner(userId);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Search restaurants
     */
    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Search restaurants by name")
    public ResponseEntity<Page<RestaurantResponse>> searchRestaurants(
        @RequestParam String query,
        Pageable pageable
    ) {
        log.info("Searching restaurants: {}", query);

        Page<RestaurantResponse> restaurants = restaurantService.searchRestaurants(query, pageable);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Get restaurants by cuisine
     */
    @GetMapping("/cuisine/{cuisineType}")
    @Operation(summary = "Get restaurants by cuisine", description = "Filter restaurants by cuisine type")
    public ResponseEntity<Page<RestaurantResponse>> getRestaurantsByCuisine(
        @PathVariable CuisineType cuisineType,
        Pageable pageable
    ) {
        log.info("Fetching restaurants by cuisine: {}", cuisineType);

        Page<RestaurantResponse> restaurants = restaurantService.getRestaurantsByCuisine(cuisineType, pageable);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Get restaurants by city
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "Get restaurants by city", description = "Filter restaurants by city")
    public ResponseEntity<Page<RestaurantResponse>> getRestaurantsByCity(
        @PathVariable String city,
        Pageable pageable
    ) {
        log.info("Fetching restaurants in city: {}", city);

        Page<RestaurantResponse> restaurants = restaurantService.getRestaurantsByCity(city, pageable);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Find restaurants near location
     */
    @GetMapping("/nearby")
    @Operation(summary = "Find nearby restaurants", description = "Find restaurants near specified location")
    public ResponseEntity<List<RestaurantResponse>> findNearbyRestaurants(
        @RequestParam BigDecimal latitude,
        @RequestParam BigDecimal longitude,
        @RequestParam(defaultValue = "10") BigDecimal radiusKm
    ) {
        log.info("Finding restaurants near lat: {}, lon: {}, radius: {}km", latitude, longitude, radiusKm);

        List<RestaurantResponse> restaurants = restaurantService.findNearLocation(latitude, longitude, radiusKm);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Get top-rated restaurants
     */
    @GetMapping("/top-rated")
    @Operation(summary = "Get top-rated restaurants", description = "Get restaurants ordered by rating")
    public ResponseEntity<Page<RestaurantResponse>> getTopRated(Pageable pageable) {
        log.info("Fetching top-rated restaurants");

        Page<RestaurantResponse> restaurants = restaurantService.getTopRatedRestaurants(pageable);
        return ResponseEntity.ok(restaurants);
    }

    /**
     * Update restaurant rating
     */
    @PostMapping("/{id}/rating")
    @Operation(summary = "Update restaurant rating", description = "Add new rating to restaurant")
    public ResponseEntity<Void> updateRating(
        @PathVariable Long id,
        @RequestParam BigDecimal rating
    ) {
        log.info("Updating rating for restaurant {}: {}", id, rating);

        restaurantService.updateRating(id, rating);
        return ResponseEntity.ok().build();
    }

    /**
     * Get restaurant statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    @Operation(summary = "Get restaurant statistics", description = "Get statistics for owner's restaurants")
    public ResponseEntity<RestaurantService.RestaurantStatistics> getStatistics(
        @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = Long.valueOf(jwt.getSubject());
        log.info("Fetching statistics for user: {}", userId);

        RestaurantService.RestaurantStatistics stats = restaurantService.getStatistics(userId);
        return ResponseEntity.ok(stats);
    }
}
