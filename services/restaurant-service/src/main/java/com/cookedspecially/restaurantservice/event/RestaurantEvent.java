package com.cookedspecially.restaurantservice.event;

import com.cookedspecially.restaurantservice.domain.Restaurant;
import com.cookedspecially.restaurantservice.domain.RestaurantStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Restaurant Event
 */
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

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }

    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Create event from restaurant entity
     */
    public static RestaurantEvent fromRestaurant(Restaurant restaurant, String eventType) {
        RestaurantEvent event = new RestaurantEvent();
        event.setEventType(eventType);
        event.setRestaurantId(restaurant.getId());
        event.setRestaurantName(restaurant.getName());
        event.setOwnerId(restaurant.getOwnerId());
        event.setStatus(restaurant.getStatus());
        event.setCuisineType(restaurant.getCuisineType() != null ? restaurant.getCuisineType().name() : null);
        event.setCity(restaurant.getCity());
        event.setLatitude(restaurant.getLatitude());
        event.setLongitude(restaurant.getLongitude());
        event.setRating(restaurant.getRating());
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
