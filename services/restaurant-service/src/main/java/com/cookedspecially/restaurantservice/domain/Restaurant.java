package com.cookedspecially.restaurantservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(nullable = false)
    @Builder.Default
    private Integer estimatedDeliveryTimeMinutes = 30;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean acceptsDelivery = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean acceptsPickup = true;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RestaurantHours> hours = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

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
