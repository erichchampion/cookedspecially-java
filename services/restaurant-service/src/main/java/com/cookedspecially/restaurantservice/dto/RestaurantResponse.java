package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {
    private Long id;
    private String name;
    private Long ownerId;
    private String description;
    private RestaurantStatus status;
    private CuisineType cuisineType;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;
    private String logoUrl;
    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal minimumOrderAmount;
    private BigDecimal deliveryFee;
    private Integer estimatedDeliveryTimeMinutes;
    private Boolean isActive;
    private Boolean acceptsDelivery;
    private Boolean acceptsPickup;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        return RestaurantResponse.builder()
            .id(restaurant.getId())
            .name(restaurant.getName())
            .ownerId(restaurant.getOwnerId())
            .description(restaurant.getDescription())
            .status(restaurant.getStatus())
            .cuisineType(restaurant.getCuisineType())
            .phoneNumber(restaurant.getPhoneNumber())
            .email(restaurant.getEmail())
            .address(restaurant.getAddress())
            .city(restaurant.getCity())
            .state(restaurant.getState())
            .zipCode(restaurant.getZipCode())
            .country(restaurant.getCountry())
            .latitude(restaurant.getLatitude())
            .longitude(restaurant.getLongitude())
            .imageUrl(restaurant.getImageUrl())
            .logoUrl(restaurant.getLogoUrl())
            .rating(restaurant.getRating())
            .reviewCount(restaurant.getReviewCount())
            .minimumOrderAmount(restaurant.getMinimumOrderAmount())
            .deliveryFee(restaurant.getDeliveryFee())
            .estimatedDeliveryTimeMinutes(restaurant.getEstimatedDeliveryTimeMinutes())
            .isActive(restaurant.getIsActive())
            .acceptsDelivery(restaurant.getAcceptsDelivery())
            .acceptsPickup(restaurant.getAcceptsPickup())
            .createdAt(restaurant.getCreatedAt())
            .updatedAt(restaurant.getUpdatedAt())
            .build();
    }
}
