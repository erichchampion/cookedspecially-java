package com.cookedspecially.restaurantservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cookedspecially.restaurantservice.domain.CuisineType;
import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import com.cookedspecially.restaurantservice.dto.CreateRestaurantRequest;
import com.cookedspecially.restaurantservice.dto.RestaurantResponse;
import com.cookedspecially.restaurantservice.dto.UpdateRestaurantRequest;
import com.cookedspecially.restaurantservice.event.RestaurantEventPublisher;
import com.cookedspecially.restaurantservice.exception.InvalidRestaurantStateException;
import com.cookedspecially.restaurantservice.exception.RestaurantNotFoundException;
import com.cookedspecially.restaurantservice.exception.UnauthorizedAccessException;
import com.cookedspecially.restaurantservice.repository.RestaurantRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Restaurant Service
 */
@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    // Constructor
    public RestaurantService(RestaurantRepository restaurantRepository,
                 RestaurantEventPublisher eventPublisher) {
        this.restaurantRepository = restaurantRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create a new restaurant
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request, Long ownerId) {
        log.info("Creating restaurant for owner: {}", ownerId);

        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.name());
        restaurant.setOwnerId(ownerId);
        restaurant.setDescription(request.description());
        restaurant.setStatus(RestaurantStatus.PENDING_APPROVAL); // All new restaurants start pending
        restaurant.setCuisineType(request.cuisineType());
        restaurant.setPhoneNumber(request.phoneNumber());
        restaurant.setEmail(request.email());
        restaurant.setAddress(request.address());
        restaurant.setCity(request.city());
        restaurant.setState(request.state());
        restaurant.setZipCode(request.zipCode());
        restaurant.setCountry(request.country());
        restaurant.setLatitude(request.latitude());
        restaurant.setLongitude(request.longitude());
        restaurant.setMinimumOrderAmount(request.minimumOrderAmount());
        restaurant.setDeliveryFee(request.deliveryFee());
        restaurant.setEstimatedDeliveryTimeMinutes(request.estimatedDeliveryTimeMinutes());
        restaurant.setIsActive(true);
        restaurant.setAcceptsDelivery(request.acceptsDelivery());
        restaurant.setAcceptsPickup(request.acceptsPickup());

        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Created restaurant with ID: {}", saved.getId());

        // Publish event
        eventPublisher.publishRestaurantCreated(saved);

        return RestaurantResponse.fromEntity(saved);
    }

    /**
     * Get restaurant by ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse getRestaurantById(Long restaurantId) {
        log.debug("Fetching restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        return RestaurantResponse.fromEntity(restaurant);
    }

    /**
     * Update restaurant
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse updateRestaurant(Long restaurantId, UpdateRestaurantRequest request, Long userId) {
        log.info("Updating restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        // Verify ownership
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, restaurantId);
        }

        // Update fields if provided
        if (request.name() != null) {
            restaurant.setName(request.name());
        }
        if (request.description() != null) {
            restaurant.setDescription(request.description());
        }
        if (request.cuisineType() != null) {
            restaurant.setCuisineType(request.cuisineType());
        }
        if (request.phoneNumber() != null) {
            restaurant.setPhoneNumber(request.phoneNumber());
        }
        if (request.email() != null) {
            restaurant.setEmail(request.email());
        }
        if (request.address() != null) {
            restaurant.setAddress(request.address());
        }
        if (request.city() != null) {
            restaurant.setCity(request.city());
        }
        if (request.state() != null) {
            restaurant.setState(request.state());
        }
        if (request.zipCode() != null) {
            restaurant.setZipCode(request.zipCode());
        }
        if (request.country() != null) {
            restaurant.setCountry(request.country());
        }
        if (request.latitude() != null) {
            restaurant.setLatitude(request.latitude());
        }
        if (request.longitude() != null) {
            restaurant.setLongitude(request.longitude());
        }
        if (request.imageUrl() != null) {
            restaurant.setImageUrl(request.imageUrl());
        }
        if (request.logoUrl() != null) {
            restaurant.setLogoUrl(request.logoUrl());
        }
        if (request.minimumOrderAmount() != null) {
            restaurant.setMinimumOrderAmount(request.minimumOrderAmount());
        }
        if (request.deliveryFee() != null) {
            restaurant.setDeliveryFee(request.deliveryFee());
        }
        if (request.estimatedDeliveryTimeMinutes() != null) {
            restaurant.setEstimatedDeliveryTimeMinutes(request.estimatedDeliveryTimeMinutes());
        }
        if (request.isActive() != null) {
            restaurant.setIsActive(request.isActive());
        }
        if (request.acceptsDelivery() != null) {
            restaurant.setAcceptsDelivery(request.acceptsDelivery());
        }
        if (request.acceptsPickup() != null) {
            restaurant.setAcceptsPickup(request.acceptsPickup());
        }

        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Updated restaurant: {}", restaurantId);

        // Publish event
        eventPublisher.publishRestaurantUpdated(updated);

        return RestaurantResponse.fromEntity(updated);
    }

    /**
     * Update restaurant status (admin only)
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse updateRestaurantStatus(Long restaurantId, RestaurantStatus newStatus) {
        log.info("Updating restaurant {} status to {}", restaurantId, newStatus);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        RestaurantStatus currentStatus = restaurant.getStatus();

        // Validate state transition
        validateStatusTransition(currentStatus, newStatus);

        restaurant.setStatus(newStatus);
        Restaurant updated = restaurantRepository.save(restaurant);

        log.info("Updated restaurant {} status from {} to {}", restaurantId, currentStatus, newStatus);

        // Publish appropriate event based on new status
        eventPublisher.publishRestaurantStatusChanged(updated, currentStatus.name());

        if (newStatus == RestaurantStatus.ACTIVE && currentStatus == RestaurantStatus.PENDING_APPROVAL) {
            eventPublisher.publishRestaurantApproved(updated);
        } else if (newStatus == RestaurantStatus.SUSPENDED) {
            eventPublisher.publishRestaurantSuspended(updated);
        } else if (newStatus == RestaurantStatus.TEMPORARILY_CLOSED) {
            eventPublisher.publishRestaurantClosed(updated);
        } else if (newStatus == RestaurantStatus.ACTIVE &&
                   (currentStatus == RestaurantStatus.TEMPORARILY_CLOSED || currentStatus == RestaurantStatus.SUSPENDED)) {
            eventPublisher.publishRestaurantReopened(updated);
        }

        return RestaurantResponse.fromEntity(updated);
    }

    /**
     * Approve restaurant (admin only)
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse approveRestaurant(Long restaurantId) {
        log.info("Approving restaurant: {}", restaurantId);
        return updateRestaurantStatus(restaurantId, RestaurantStatus.ACTIVE);
    }

    /**
     * Suspend restaurant (admin only)
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse suspendRestaurant(Long restaurantId) {
        log.info("Suspending restaurant: {}", restaurantId);
        return updateRestaurantStatus(restaurantId, RestaurantStatus.SUSPENDED);
    }

    /**
     * Close restaurant temporarily
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse closeTemporarily(Long restaurantId, Long userId) {
        log.info("Temporarily closing restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        // Verify ownership
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, restaurantId);
        }

        return updateRestaurantStatus(restaurantId, RestaurantStatus.TEMPORARILY_CLOSED);
    }

    /**
     * Reopen restaurant
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse reopenRestaurant(Long restaurantId, Long userId) {
        log.info("Reopening restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        // Verify ownership
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new UnauthorizedAccessException(userId, restaurantId);
        }

        return updateRestaurantStatus(restaurantId, RestaurantStatus.ACTIVE);
    }

    /**
     * Get restaurants by owner
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> getRestaurantsByOwner(Long ownerId) {
        log.debug("Fetching restaurants for owner: {}", ownerId);

        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);

        return restaurants.stream()
            .map(RestaurantResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Search restaurants by name
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> searchRestaurants(String searchTerm, Pageable pageable) {
        log.debug("Searching restaurants: {}", searchTerm);

        Page<Restaurant> restaurants = restaurantRepository.searchByName(searchTerm, pageable);

        return restaurants.map(RestaurantResponse::fromEntity);
    }

    /**
     * Get restaurants by cuisine type
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getRestaurantsByCuisine(CuisineType cuisineType, Pageable pageable) {
        log.debug("Fetching restaurants by cuisine: {}", cuisineType);

        Page<Restaurant> restaurants = restaurantRepository
            .findByCuisineTypeAndStatusAndIsActiveTrue(cuisineType, RestaurantStatus.ACTIVE, pageable);

        return restaurants.map(RestaurantResponse::fromEntity);
    }

    /**
     * Get restaurants by city
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getRestaurantsByCity(String city, Pageable pageable) {
        log.debug("Fetching restaurants in city: {}", city);

        Page<Restaurant> restaurants = restaurantRepository
            .findByCityAndStatusAndIsActiveTrue(city, RestaurantStatus.ACTIVE, pageable);

        return restaurants.map(RestaurantResponse::fromEntity);
    }

    /**
     * Find restaurants near location
     */
    @Transactional(readOnly = true)
    public List<RestaurantResponse> findNearLocation(BigDecimal latitude, BigDecimal longitude,
                                                      BigDecimal radiusKm) {
        log.debug("Finding restaurants near lat: {}, lon: {}, radius: {}km", latitude, longitude, radiusKm);

        // Calculate bounding box (simplified - 1 degree ~ 111km)
        BigDecimal latDelta = radiusKm.divide(BigDecimal.valueOf(111), 7, BigDecimal.ROUND_HALF_UP);
        BigDecimal lonDelta = radiusKm.divide(BigDecimal.valueOf(111), 7, BigDecimal.ROUND_HALF_UP);

        BigDecimal minLat = latitude.subtract(latDelta);
        BigDecimal maxLat = latitude.add(latDelta);
        BigDecimal minLon = longitude.subtract(lonDelta);
        BigDecimal maxLon = longitude.add(lonDelta);

        List<Restaurant> restaurants = restaurantRepository.findNearLocation(minLat, maxLat, minLon, maxLon);

        return restaurants.stream()
            .map(RestaurantResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get top-rated restaurants
     */
    @Transactional(readOnly = true)
    public Page<RestaurantResponse> getTopRatedRestaurants(Pageable pageable) {
        log.debug("Fetching top-rated restaurants");

        Page<Restaurant> restaurants = restaurantRepository
            .findByStatusAndIsActiveTrueOrderByRatingDesc(RestaurantStatus.ACTIVE, pageable);

        return restaurants.map(RestaurantResponse::fromEntity);
    }

    /**
     * Update restaurant rating
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public void updateRating(Long restaurantId, BigDecimal newRating) {
        log.info("Updating rating for restaurant: {}", restaurantId);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

        restaurant.updateRating(newRating);
        Restaurant updated = restaurantRepository.save(restaurant);

        log.info("Updated restaurant {} rating to {}", restaurantId, restaurant.getRating());

        // Publish event
        eventPublisher.publishRatingUpdated(updated);
    }

    /**
     * Get restaurant statistics
     */
    @Transactional(readOnly = true)
    public RestaurantStatistics getStatistics(Long ownerId) {
        log.debug("Fetching statistics for owner: {}", ownerId);

        long totalRestaurants = restaurantRepository.countByOwnerIdAndStatus(ownerId, RestaurantStatus.ACTIVE);
        long pendingApproval = restaurantRepository.countByOwnerIdAndStatus(ownerId, RestaurantStatus.PENDING_APPROVAL);

        return new RestaurantStatistics(totalRestaurants, pendingApproval);
    }

    /**
     * Validate status transition
     */
    private void validateStatusTransition(RestaurantStatus current, RestaurantStatus requested) {
        // PERMANENTLY_CLOSED is terminal - cannot transition from it
        if (current == RestaurantStatus.PERMANENTLY_CLOSED) {
            throw new InvalidRestaurantStateException(null, current.name(), requested.name());
        }

        // Cannot transition to PENDING_APPROVAL from other states
        if (requested == RestaurantStatus.PENDING_APPROVAL && current != RestaurantStatus.PENDING_APPROVAL) {
            throw new InvalidRestaurantStateException(null, current.name(), requested.name());
        }
    }

    /**
     * Restaurant statistics DTO
     */
    public record RestaurantStatistics(
        long totalActiveRestaurants,
        long pendingApproval
    ) {}
}
