package com.cookedspecially.restaurantservice.dto;

import com.cookedspecially.restaurantservice.domain.MenuItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MenuItemResponse {
    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private Boolean isAvailable;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;
    private Integer calories;
    private Integer preparationTimeMinutes;
    private LocalDateTime createdAt;

    // Constructors
    public MenuItemResponse() {
    }

    public MenuItemResponse(Long id,
                 Long restaurantId,
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
                 LocalDateTime createdAt) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.isVegetarian = isVegetarian;
        this.isVegan = isVegan;
        this.isGlutenFree = isGlutenFree;
        this.isSpicy = isSpicy;
        this.calories = calories;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public static MenuItemResponse fromEntity(MenuItem item) {
        MenuItemResponse response = new MenuItemResponse();
        response.setId(item.getId());
        response.setRestaurantId(item.getRestaurant() != null ? item.getRestaurant().getId() : null);
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setCategory(item.getCategory());
        response.setImageUrl(item.getImageUrl());
        response.setIsAvailable(item.getIsAvailable());
        response.setIsVegetarian(item.getIsVegetarian());
        response.setIsVegan(item.getIsVegan());
        response.setIsGlutenFree(item.getIsGlutenFree());
        response.setIsSpicy(item.getIsSpicy());
        response.setCalories(item.getCalories());
        response.setPreparationTimeMinutes(item.getPreparationTimeMinutes());
        response.setCreatedAt(item.getCreatedAt());
        return response;
    }
}
