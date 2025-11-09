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

/**
 * Menu Item Entity
 */
@Entity
@Table(name = "menu_items", indexes = {
    @Index(name = "idx_restaurant_id", columnList = "restaurant_id"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_available", columnList = "isAvailable")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 100)
    private String category;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVegetarian = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isVegan = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isGlutenFree = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isSpicy = false;

    @Column
    private Integer calories;

    @Column
    private Integer preparationTimeMinutes;

    @Column(nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Check if item can be ordered
     */
    public boolean canBeOrdered() {
        return isAvailable && restaurant != null && restaurant.isCurrentlyOpen();
    }
}
