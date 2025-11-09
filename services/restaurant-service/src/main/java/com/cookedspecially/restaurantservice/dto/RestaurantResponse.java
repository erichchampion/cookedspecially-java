package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // Constructors
    public RestaurantResponse() {
    }

    public RestaurantResponse(Long id,
                 String name,
                 Long ownerId,
                 String description,
                 RestaurantStatus status,
                 CuisineType cuisineType,
                 String phoneNumber,
                 String email,
                 String address,
                 String city,
                 String state,
                 String zipCode,
                 String country,
                 BigDecimal latitude,
                 BigDecimal longitude,
                 String imageUrl,
                 String logoUrl,
                 BigDecimal rating,
                 Integer reviewCount,
                 BigDecimal minimumOrderAmount,
                 BigDecimal deliveryFee,
                 Integer estimatedDeliveryTimeMinutes,
                 Boolean isActive,
                 Boolean acceptsDelivery,
                 Boolean acceptsPickup,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.status = status;
        this.cuisineType = cuisineType;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.logoUrl = logoUrl;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.minimumOrderAmount = minimumOrderAmount;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes;
        this.isActive = isActive;
        this.acceptsDelivery = acceptsDelivery;
        this.acceptsPickup = acceptsPickup;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RestaurantStatus getStatus() {
        return status;
    }

    public void setStatus(RestaurantStatus status) {
        this.status = status;
    }

    public CuisineType getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(CuisineType cuisineType) {
        this.cuisineType = cuisineType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Integer getEstimatedDeliveryTimeMinutes() {
        return estimatedDeliveryTimeMinutes;
    }

    public void setEstimatedDeliveryTimeMinutes(Integer estimatedDeliveryTimeMinutes) {
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getAcceptsDelivery() {
        return acceptsDelivery;
    }

    public void setAcceptsDelivery(Boolean acceptsDelivery) {
        this.acceptsDelivery = acceptsDelivery;
    }

    public Boolean getAcceptsPickup() {
        return acceptsPickup;
    }

    public void setAcceptsPickup(Boolean acceptsPickup) {
        this.acceptsPickup = acceptsPickup;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    public static RestaurantResponse fromEntity(Restaurant restaurant) {
        RestaurantResponse response = new RestaurantResponse();
        response.setId(restaurant.getId());
        response.setName(restaurant.getName());
        response.setOwnerId(restaurant.getOwnerId());
        response.setDescription(restaurant.getDescription());
        response.setStatus(restaurant.getStatus());
        response.setCuisineType(restaurant.getCuisineType());
        response.setPhoneNumber(restaurant.getPhoneNumber());
        response.setEmail(restaurant.getEmail());
        response.setAddress(restaurant.getAddress());
        response.setCity(restaurant.getCity());
        response.setState(restaurant.getState());
        response.setZipCode(restaurant.getZipCode());
        response.setCountry(restaurant.getCountry());
        response.setLatitude(restaurant.getLatitude());
        response.setLongitude(restaurant.getLongitude());
        response.setImageUrl(restaurant.getImageUrl());
        response.setLogoUrl(restaurant.getLogoUrl());
        response.setRating(restaurant.getRating());
        response.setReviewCount(restaurant.getReviewCount());
        response.setMinimumOrderAmount(restaurant.getMinimumOrderAmount());
        response.setDeliveryFee(restaurant.getDeliveryFee());
        response.setEstimatedDeliveryTimeMinutes(restaurant.getEstimatedDeliveryTimeMinutes());
        response.setIsActive(restaurant.getIsActive());
        response.setAcceptsDelivery(restaurant.getAcceptsDelivery());
        response.setAcceptsPickup(restaurant.getAcceptsPickup());
        response.setCreatedAt(restaurant.getCreatedAt());
        response.setUpdatedAt(restaurant.getUpdatedAt());
        return response;
    }
}
