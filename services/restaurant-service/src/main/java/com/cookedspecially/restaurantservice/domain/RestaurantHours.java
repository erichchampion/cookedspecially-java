package com.cookedspecially.restaurantservice.domain;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Restaurant Operating Hours Entity
 */
@Entity
@Table(name = "restaurant_hours", indexes = {
    @Index(name = "idx_restaurant_day", columnList = "restaurant_id,dayOfWeek")
})
public class RestaurantHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime openTime;

    @Column(nullable = false)
    private LocalTime closeTime;

    @Column(nullable = false)
    private Boolean isClosed = false;

    // Constructors
    public RestaurantHours() {
    }

    public RestaurantHours(Long id,
                 Restaurant restaurant,
                 DayOfWeek dayOfWeek,
                 LocalTime openTime,
                 LocalTime closeTime,
                 Boolean isClosed) {
        this.id = id;
        this.restaurant = restaurant;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.isClosed = isClosed != null ? isClosed : false;
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

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public Boolean getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    /**
     * Check if restaurant is open at given time
     */
    public boolean isOpenAt(LocalTime time) {
        if (isClosed) {
            return false;
        }
        return !time.isBefore(openTime) && !time.isAfter(closeTime);
    }
}
