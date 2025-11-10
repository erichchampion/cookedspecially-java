package com.cookedspecially.customerservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer preferences and settings
 */
@Entity
@Table(name = "customer_preferences")
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
    private Integer defaultTipPercentage = 10;

    // UI preferences
    @Column(length = 10)
    private String language = "en";

    @Column(length = 10)
    private String currency = "USD";

    @Column(name = "dark_mode")
    private Boolean darkMode = false;

    // Privacy settings
    @Column(name = "show_online_status")
    private Boolean showOnlineStatus = true;

    @Column(name = "share_location")
    private Boolean shareLocation = true;

    @Column(name = "save_order_history")
    private Boolean saveOrderHistory = true;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // No-args constructor
    public CustomerPreference() {
    }

    // All-args constructor
    public CustomerPreference(Long id, Customer customer, String dietaryRestrictions,
                             String favoriteCuisines, String allergies,
                             String preferredPaymentMethod, Integer defaultTipPercentage,
                             String language, String currency, Boolean darkMode,
                             Boolean showOnlineStatus, Boolean shareLocation,
                             Boolean saveOrderHistory, LocalDateTime createdAt,
                             LocalDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
        this.dietaryRestrictions = dietaryRestrictions;
        this.favoriteCuisines = favoriteCuisines;
        this.allergies = allergies;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.defaultTipPercentage = defaultTipPercentage != null ? defaultTipPercentage : 10;
        this.language = language != null ? language : "en";
        this.currency = currency != null ? currency : "USD";
        this.darkMode = darkMode != null ? darkMode : false;
        this.showOnlineStatus = showOnlineStatus != null ? showOnlineStatus : true;
        this.shareLocation = shareLocation != null ? shareLocation : true;
        this.saveOrderHistory = saveOrderHistory != null ? saveOrderHistory : true;
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

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getDietaryRestrictions() {
        return dietaryRestrictions;
    }

    public void setDietaryRestrictions(String dietaryRestrictions) {
        this.dietaryRestrictions = dietaryRestrictions;
    }

    public String getFavoriteCuisines() {
        return favoriteCuisines;
    }

    public void setFavoriteCuisines(String favoriteCuisines) {
        this.favoriteCuisines = favoriteCuisines;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public Integer getDefaultTipPercentage() {
        return defaultTipPercentage;
    }

    public void setDefaultTipPercentage(Integer defaultTipPercentage) {
        this.defaultTipPercentage = defaultTipPercentage;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getDarkMode() {
        return darkMode;
    }

    public void setDarkMode(Boolean darkMode) {
        this.darkMode = darkMode;
    }

    public Boolean getShowOnlineStatus() {
        return showOnlineStatus;
    }

    public void setShowOnlineStatus(Boolean showOnlineStatus) {
        this.showOnlineStatus = showOnlineStatus;
    }

    public Boolean getShareLocation() {
        return shareLocation;
    }

    public void setShareLocation(Boolean shareLocation) {
        this.shareLocation = shareLocation;
    }

    public Boolean getSaveOrderHistory() {
        return saveOrderHistory;
    }

    public void setSaveOrderHistory(Boolean saveOrderHistory) {
        this.saveOrderHistory = saveOrderHistory;
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

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerPreference that = (CustomerPreference) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(customer, that.customer) &&
               Objects.equals(dietaryRestrictions, that.dietaryRestrictions) &&
               Objects.equals(favoriteCuisines, that.favoriteCuisines) &&
               Objects.equals(allergies, that.allergies) &&
               Objects.equals(preferredPaymentMethod, that.preferredPaymentMethod) &&
               Objects.equals(defaultTipPercentage, that.defaultTipPercentage) &&
               Objects.equals(language, that.language) &&
               Objects.equals(currency, that.currency) &&
               Objects.equals(darkMode, that.darkMode) &&
               Objects.equals(showOnlineStatus, that.showOnlineStatus) &&
               Objects.equals(shareLocation, that.shareLocation) &&
               Objects.equals(saveOrderHistory, that.saveOrderHistory) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, dietaryRestrictions, favoriteCuisines, allergies,
                          preferredPaymentMethod, defaultTipPercentage, language, currency,
                          darkMode, showOnlineStatus, shareLocation, saveOrderHistory,
                          createdAt, updatedAt);
    }

    // toString
    @Override
    public String toString() {
        return "CustomerPreference{" +
               "id=" + id +
               ", customer=" + customer +
               ", dietaryRestrictions='" + dietaryRestrictions + '\'' +
               ", favoriteCuisines='" + favoriteCuisines + '\'' +
               ", allergies='" + allergies + '\'' +
               ", preferredPaymentMethod='" + preferredPaymentMethod + '\'' +
               ", defaultTipPercentage=" + defaultTipPercentage +
               ", language='" + language + '\'' +
               ", currency='" + currency + '\'' +
               ", darkMode=" + darkMode +
               ", showOnlineStatus=" + showOnlineStatus +
               ", shareLocation=" + shareLocation +
               ", saveOrderHistory=" + saveOrderHistory +
               ", createdAt=" + createdAt +
               ", updatedAt=" + updatedAt +
               '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Customer customer;
        private String dietaryRestrictions;
        private String favoriteCuisines;
        private String allergies;
        private String preferredPaymentMethod;
        private Integer defaultTipPercentage = 10;
        private String language = "en";
        private String currency = "USD";
        private Boolean darkMode = false;
        private Boolean showOnlineStatus = true;
        private Boolean shareLocation = true;
        private Boolean saveOrderHistory = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
            return this;
        }

        public Builder dietaryRestrictions(String dietaryRestrictions) {
            this.dietaryRestrictions = dietaryRestrictions;
            return this;
        }

        public Builder favoriteCuisines(String favoriteCuisines) {
            this.favoriteCuisines = favoriteCuisines;
            return this;
        }

        public Builder allergies(String allergies) {
            this.allergies = allergies;
            return this;
        }

        public Builder preferredPaymentMethod(String preferredPaymentMethod) {
            this.preferredPaymentMethod = preferredPaymentMethod;
            return this;
        }

        public Builder defaultTipPercentage(Integer defaultTipPercentage) {
            this.defaultTipPercentage = defaultTipPercentage;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder darkMode(Boolean darkMode) {
            this.darkMode = darkMode;
            return this;
        }

        public Builder showOnlineStatus(Boolean showOnlineStatus) {
            this.showOnlineStatus = showOnlineStatus;
            return this;
        }

        public Builder shareLocation(Boolean shareLocation) {
            this.shareLocation = shareLocation;
            return this;
        }

        public Builder saveOrderHistory(Boolean saveOrderHistory) {
            this.saveOrderHistory = saveOrderHistory;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CustomerPreference build() {
            CustomerPreference customerPreference = new CustomerPreference();
            customerPreference.id = this.id;
            customerPreference.customer = this.customer;
            customerPreference.dietaryRestrictions = this.dietaryRestrictions;
            customerPreference.favoriteCuisines = this.favoriteCuisines;
            customerPreference.allergies = this.allergies;
            customerPreference.preferredPaymentMethod = this.preferredPaymentMethod;
            customerPreference.defaultTipPercentage = this.defaultTipPercentage;
            customerPreference.language = this.language;
            customerPreference.currency = this.currency;
            customerPreference.darkMode = this.darkMode;
            customerPreference.showOnlineStatus = this.showOnlineStatus;
            customerPreference.shareLocation = this.shareLocation;
            customerPreference.saveOrderHistory = this.saveOrderHistory;
            customerPreference.createdAt = this.createdAt;
            customerPreference.updatedAt = this.updatedAt;
            return customerPreference;
        }
    }
}
