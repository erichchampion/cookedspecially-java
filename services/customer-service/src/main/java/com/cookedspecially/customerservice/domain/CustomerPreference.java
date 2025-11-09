package com.cookedspecially.customerservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Customer preferences and settings
 */
@Entity
@Table(name = "customer_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    @JsonIgnore
    private Customer customer;

    // Dietary preferences
    @Column(name = "dietary_restrictions", length = 500)
    private String dietaryRestrictions;  // Comma-separated: vegetarian, vegan, gluten-free, etc.

    @Column(name = "favorite_cuisines", length = 500)
    private String favoriteCuisines;  // Comma-separated cuisine types

    @Column(name = "allergies", length = 500)
    private String allergies;

    // Ordering preferences
    @Column(name = "preferred_payment_method", length = 50)
    private String preferredPaymentMethod;  // CARD, CASH, WALLET

    @Column(name = "default_tip_percentage")
    @Builder.Default
    private Integer defaultTipPercentage = 10;

    // UI preferences
    @Column(length = 10)
    @Builder.Default
    private String language = "en";

    @Column(length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "dark_mode")
    @Builder.Default
    private Boolean darkMode = false;

    // Privacy settings
    @Column(name = "show_online_status")
    @Builder.Default
    private Boolean showOnlineStatus = true;

    @Column(name = "share_location")
    @Builder.Default
    private Boolean shareLocation = true;

    @Column(name = "save_order_history")
    @Builder.Default
    private Boolean saveOrderHistory = true;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
