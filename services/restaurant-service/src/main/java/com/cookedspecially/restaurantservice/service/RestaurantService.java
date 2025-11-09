package com.cookedspecially.restaurantservice.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantEventPublisher eventPublisher;

    /**
     * Create a new restaurant
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request, Long ownerId) {
        log.info("Creating restaurant for owner: {}", ownerId);

        Restaurant restaurant = Restaurant.builder()
            .name(request.getName())
            .ownerId(ownerId)
            .description(request.getDescription())
            .status(RestaurantStatus.PENDING_APPROVAL) // All new restaurants start pending
            .cuisineType(request.getCuisineType())
            .phoneNumber(request.getPhoneNumber())
            .email(request.getEmail())
            .address(request.getAddress())
            .city(request.getCity())
            .state(request.getState())
            .zipCode(request.getZipCode())
            .country(request.getCountry())
            .latitude(request.getLatitude())
            .longitude(request.getLongitude())
            .imageUrl(request.getImageUrl())
            .logoUrl(request.getLogoUrl())
            .minimumOrderAmount(request.getMinimumOrderAmount())
            .deliveryFee(request.getDeliveryFee())
            .estimatedDeliveryTimeMinutes(request.getEstimatedDeliveryTimeMinutes())
            .isActive(true)
            .acceptsDelivery(request.getAcceptsDelivery())
            .acceptsPickup(request.getAcceptsPickup())
            .build();

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
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getCuisineType() != null) {
            restaurant.setCuisineType(request.getCuisineType());
        }
        if (request.getPhoneNumber() != null) {
            restaurant.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getCity() != null) {
            restaurant.setCity(request.getCity());
        }
        if (request.getState() != null) {
            restaurant.setState(request.getState());
        }
        if (request.getZipCode() != null) {
            restaurant.setZipCode(request.getZipCode());
        }
        if (request.getCountry() != null) {
            restaurant.setCountry(request.getCountry());
        }
        if (request.getLatitude() != null) {
            restaurant.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            restaurant.setLongitude(request.getLongitude());
        }
        if (request.getImageUrl() != null) {
            restaurant.setImageUrl(request.getImageUrl());
        }
        if (request.getLogoUrl() != null) {
            restaurant.setLogoUrl(request.getLogoUrl());
        }
        if (request.getMinimumOrderAmount() != null) {
            restaurant.setMinimumOrderAmount(request.getMinimumOrderAmount());
        }
        if (request.getDeliveryFee() != null) {
            restaurant.setDeliveryFee(request.getDeliveryFee());
        }
        if (request.getEstimatedDeliveryTimeMinutes() != null) {
            restaurant.setEstimatedDeliveryTimeMinutes(request.getEstimatedDeliveryTimeMinutes());
        }
        if (request.getIsActive() != null) {
            restaurant.setIsActive(request.getIsActive());
        }
        if (request.getAcceptsDelivery() != null) {
            restaurant.setAcceptsDelivery(request.getAcceptsDelivery());
        }
        if (request.getAcceptsPickup() != null) {
            restaurant.setAcceptsPickup(request.getAcceptsPickup());
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
