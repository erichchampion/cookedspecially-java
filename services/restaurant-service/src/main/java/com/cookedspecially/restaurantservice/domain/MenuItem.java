package com.cookedspecially.restaurantservice.domain;

import jakarta.persistence.*;
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
    private Boolean isAvailable = true;

    @Column(nullable = false)
    private Boolean isVegetarian = false;

    @Column(nullable = false)
    private Boolean isVegan = false;

    @Column(nullable = false)
    private Boolean isGlutenFree = false;

    @Column(nullable = false)
    private Boolean isSpicy = false;

    @Column
    private Integer spiceLevel;

    @Column
    private Integer calories;

    @Column
    private Integer preparationTimeMinutes;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MenuItem() {
    }

    public MenuItem(Long id,
                 Restaurant restaurant,
                 String name,
                 String description,
                 BigDecimal price,
                 String category,
                 String imageUrl,
                 Boolean isAvailable,
                 Boolean isVegetarian,
                 Boolean isVegan,
                 Boolean isGlutenFree,
                 Boolean isSpicy,
                 Integer calories,
                 Integer preparationTimeMinutes,
                 Integer displayOrder,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt) {
        this.id = id;
        this.restaurant = restaurant;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable != null ? isAvailable : true;
        this.isVegetarian = isVegetarian != null ? isVegetarian : false;
        this.isVegan = isVegan != null ? isVegan : false;
        this.isGlutenFree = isGlutenFree != null ? isGlutenFree : false;
        this.isSpicy = isSpicy != null ? isSpicy : false;
        this.calories = calories;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
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

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsVegetarian() {
        return isVegetarian;
    }

    public void setIsVegetarian(Boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

    public Boolean getIsVegan() {
        return isVegan;
    }

    public void setIsVegan(Boolean isVegan) {
        this.isVegan = isVegan;
    }

    public Boolean getIsGlutenFree() {
        return isGlutenFree;
    }

    public void setIsGlutenFree(Boolean isGlutenFree) {
        this.isGlutenFree = isGlutenFree;
    }

    public Boolean getIsSpicy() {
        return isSpicy;
    }

    public void setIsSpicy(Boolean isSpicy) {
        this.isSpicy = isSpicy;
    }

    public Integer getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(Integer spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
     * Check if item can be ordered
     */
    public boolean canBeOrdered() {
        return isAvailable && restaurant != null && restaurant.isCurrentlyOpen();
    }
}
