package com.cookedspecially.restaurantservice.event;

import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Restaurant Event
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantEvent {

    private String eventType;
    private Long restaurantId;
    private String restaurantName;
    private Long ownerId;
    private RestaurantStatus status;
    private String cuisineType;
    private String city;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal rating;
    private LocalDateTime timestamp;

    /**
     * Event types
     */
    public static class EventType {
        public static final String RESTAURANT_CREATED = "RESTAURANT_CREATED";
        public static final String RESTAURANT_UPDATED = "RESTAURANT_UPDATED";
        public static final String RESTAURANT_STATUS_CHANGED = "RESTAURANT_STATUS_CHANGED";
        public static final String RESTAURANT_APPROVED = "RESTAURANT_APPROVED";
        public static final String RESTAURANT_SUSPENDED = "RESTAURANT_SUSPENDED";
        public static final String RESTAURANT_CLOSED = "RESTAURANT_CLOSED";
        public static final String RESTAURANT_REOPENED = "RESTAURANT_REOPENED";
        public static final String MENU_UPDATED = "MENU_UPDATED";
        public static final String RATING_UPDATED = "RATING_UPDATED";
    }

    /**
     * Create event from restaurant entity
     */
    public static RestaurantEvent fromRestaurant(Restaurant restaurant, String eventType) {
        return RestaurantEvent.builder()
            .eventType(eventType)
            .restaurantId(restaurant.getId())
            .restaurantName(restaurant.getName())
            .ownerId(restaurant.getOwnerId())
            .status(restaurant.getStatus())
            .cuisineType(restaurant.getCuisineType() != null ? restaurant.getCuisineType().name() : null)
            .city(restaurant.getCity())
            .latitude(restaurant.getLatitude())
            .longitude(restaurant.getLongitude())
            .rating(restaurant.getRating())
            .timestamp(LocalDateTime.now())
            .build();
    }
}
