package com.cookedspecially.restaurantservice.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Restaurant Entity
 */
@Entity
@Table(name = "restaurants", indexes = {
    @Index(name = "idx_owner_id", columnList = "ownerId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_cuisine_type", columnList = "cuisineType"),
    @Index(name = "idx_location", columnList = "latitude,longitude")
})
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Long ownerId;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RestaurantStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CuisineType cuisineType;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 200)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 50)
    private String state;

    @Column(length = 20)
    private String zipCode;

    @Column(length = 50)
    private String country;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String logoUrl;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false)
    private Integer estimatedDeliveryTimeMinutes = 30;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false)
    private Boolean acceptsDelivery = true;

    @Column(nullable = false)
    private Boolean acceptsPickup = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantHours> hours = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public Restaurant() {
    }

    public Restaurant(Long id,
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
                 List<MenuItem> menuItems,
                 List<RestaurantHours> hours,
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
        this.rating = rating != null ? rating : BigDecimal.ZERO;
        this.reviewCount = reviewCount != null ? reviewCount : 0;
        this.minimumOrderAmount = minimumOrderAmount;
        this.deliveryFee = deliveryFee;
        this.estimatedDeliveryTimeMinutes = estimatedDeliveryTimeMinutes != null ? estimatedDeliveryTimeMinutes : 30;
        this.isActive = isActive != null ? isActive : true;
        this.acceptsDelivery = acceptsDelivery != null ? acceptsDelivery : true;
        this.acceptsPickup = acceptsPickup != null ? acceptsPickup : true;
        this.menuItems = menuItems != null ? menuItems : new ArrayList<>();
        this.hours = hours != null ? hours : new ArrayList<>();
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

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<RestaurantHours> getHours() {
        return hours;
    }

    public void setHours(List<RestaurantHours> hours) {
        this.hours = hours;
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

    /**
     * Add menu item to restaurant
     */
    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
        item.setRestaurant(this);
    }

    /**
     * Remove menu item from restaurant
     */
    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item);
        item.setRestaurant(null);
    }

    /**
     * Add operating hours
     */
    public void addHours(RestaurantHours restaurantHours) {
        hours.add(restaurantHours);
        restaurantHours.setRestaurant(this);
    }

    /**
     * Update rating
     */
    public void updateRating(BigDecimal newRating) {
        if (reviewCount == 0) {
            this.rating = newRating;
            this.reviewCount = 1;
        } else {
            BigDecimal totalRating = this.rating.multiply(BigDecimal.valueOf(reviewCount));
            totalRating = totalRating.add(newRating);
            this.reviewCount++;
            this.rating = totalRating.divide(BigDecimal.valueOf(reviewCount), 2, java.math.RoundingMode.HALF_UP);
        }
    }

    /**
     * Check if restaurant is currently open
     */
    public boolean isCurrentlyOpen() {
        // This would check against restaurant hours
        // Simplified implementation for now
        return status == RestaurantStatus.ACTIVE && isActive;
    }
}
