package com.cookedspecially.customerservice.dto;

import java.util.Objects;

/**
 * Customer preference response DTO
 */
public class CustomerPreferenceResponse {
    private Long id;
    private String dietaryRestrictions;
    private String favoriteCuisines;
    private String allergies;
    private String preferredPaymentMethod;
    private Integer defaultTipPercentage;
    private String language;
    private String currency;
    private Boolean darkMode;
    private Boolean showOnlineStatus;
    private Boolean shareLocation;
    private Boolean saveOrderHistory;

    public CustomerPreferenceResponse() {
    }

    public CustomerPreferenceResponse(Long id, String dietaryRestrictions, String favoriteCuisines,
                                     String allergies, String preferredPaymentMethod,
                                     Integer defaultTipPercentage, String language, String currency,
                                     Boolean darkMode, Boolean showOnlineStatus, Boolean shareLocation,
                                     Boolean saveOrderHistory) {
        this.id = id;
        this.dietaryRestrictions = dietaryRestrictions;
        this.favoriteCuisines = favoriteCuisines;
        this.allergies = allergies;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.defaultTipPercentage = defaultTipPercentage;
        this.language = language;
        this.currency = currency;
        this.darkMode = darkMode;
        this.showOnlineStatus = showOnlineStatus;
        this.shareLocation = shareLocation;
        this.saveOrderHistory = saveOrderHistory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerPreferenceResponse that = (CustomerPreferenceResponse) o;
        return Objects.equals(id, that.id) &&
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
               Objects.equals(saveOrderHistory, that.saveOrderHistory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dietaryRestrictions, favoriteCuisines, allergies,
                          preferredPaymentMethod, defaultTipPercentage, language, currency,
                          darkMode, showOnlineStatus, shareLocation, saveOrderHistory);
    }

    @Override
    public String toString() {
        return "CustomerPreferenceResponse{" +
               "id=" + id +
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
               '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String dietaryRestrictions;
        private String favoriteCuisines;
        private String allergies;
        private String preferredPaymentMethod;
        private Integer defaultTipPercentage;
        private String language;
        private String currency;
        private Boolean darkMode;
        private Boolean showOnlineStatus;
        private Boolean shareLocation;
        private Boolean saveOrderHistory;

        Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
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

        public CustomerPreferenceResponse build() {
            return new CustomerPreferenceResponse(id, dietaryRestrictions, favoriteCuisines,
                                                 allergies, preferredPaymentMethod,
                                                 defaultTipPercentage, language, currency,
                                                 darkMode, showOnlineStatus, shareLocation,
                                                 saveOrderHistory);
        }

        @Override
        public String toString() {
            return "CustomerPreferenceResponse.Builder{" +
                   "id=" + id +
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
                   '}';
        }
    }
}
