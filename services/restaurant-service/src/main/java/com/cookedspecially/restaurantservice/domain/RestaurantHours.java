package com.cookedspecially.restaurantservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Restaurant Operating Hours Entity
 */
@Entity
@Table(name = "restaurant_hours", indexes = {
    @Index(name = "idx_restaurant_day", columnList = "restaurant_id,dayOfWeek")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Boolean isClosed = false;

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
