package com.cookedspecially.customerservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Address response DTO
 */
public class AddressResponse {
    private Long id;
    private String addressType;
    private String label;
    private String streetAddress;
    private String apartmentUnit;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String deliveryInstructions;
    private Boolean isDefault;
    private String fullAddress;
    private LocalDateTime createdAt;

    // No-args constructor
    public AddressResponse() {
    }

    // All-args constructor
    public AddressResponse(Long id, String addressType, String label, String streetAddress,
                          String apartmentUnit, String city, String state, String postalCode,
                          String country, BigDecimal latitude, BigDecimal longitude,
                          String deliveryInstructions, Boolean isDefault, String fullAddress,
                          LocalDateTime createdAt) {
        this.id = id;
        this.addressType = addressType;
        this.label = label;
        this.streetAddress = streetAddress;
        this.apartmentUnit = apartmentUnit;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryInstructions = deliveryInstructions;
        this.isDefault = isDefault;
        this.fullAddress = fullAddress;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getAddressType() {
        return addressType;
    }

    public String getLabel() {
        return label;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getApartmentUnit() {
        return apartmentUnit;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setApartmentUnit(String apartmentUnit) {
        this.apartmentUnit = apartmentUnit;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressResponse that = (AddressResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(addressType, that.addressType) &&
               Objects.equals(label, that.label) &&
               Objects.equals(streetAddress, that.streetAddress) &&
               Objects.equals(apartmentUnit, that.apartmentUnit) &&
               Objects.equals(city, that.city) &&
               Objects.equals(state, that.state) &&
               Objects.equals(postalCode, that.postalCode) &&
               Objects.equals(country, that.country) &&
               Objects.equals(latitude, that.latitude) &&
               Objects.equals(longitude, that.longitude) &&
               Objects.equals(deliveryInstructions, that.deliveryInstructions) &&
               Objects.equals(isDefault, that.isDefault) &&
               Objects.equals(fullAddress, that.fullAddress) &&
               Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, addressType, label, streetAddress, apartmentUnit, city, state,
                          postalCode, country, latitude, longitude, deliveryInstructions,
                          isDefault, fullAddress, createdAt);
    }

    // toString
    @Override
    public String toString() {
        return "AddressResponse{" +
               "id=" + id +
               ", addressType='" + addressType + '\'' +
               ", label='" + label + '\'' +
               ", streetAddress='" + streetAddress + '\'' +
               ", apartmentUnit='" + apartmentUnit + '\'' +
               ", city='" + city + '\'' +
               ", state='" + state + '\'' +
               ", postalCode='" + postalCode + '\'' +
               ", country='" + country + '\'' +
               ", latitude=" + latitude +
               ", longitude=" + longitude +
               ", deliveryInstructions='" + deliveryInstructions + '\'' +
               ", isDefault=" + isDefault +
               ", fullAddress='" + fullAddress + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String addressType;
        private String label;
        private String streetAddress;
        private String apartmentUnit;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private BigDecimal latitude;
        private BigDecimal longitude;
        private String deliveryInstructions;
        private Boolean isDefault;
        private String fullAddress;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder addressType(String addressType) {
            this.addressType = addressType;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder streetAddress(String streetAddress) {
            this.streetAddress = streetAddress;
            return this;
        }

        public Builder apartmentUnit(String apartmentUnit) {
            this.apartmentUnit = apartmentUnit;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder latitude(BigDecimal latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(BigDecimal longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder deliveryInstructions(String deliveryInstructions) {
            this.deliveryInstructions = deliveryInstructions;
            return this;
        }

        public Builder isDefault(Boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Builder fullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public AddressResponse build() {
            return new AddressResponse(id, addressType, label, streetAddress, apartmentUnit,
                                     city, state, postalCode, country, latitude, longitude,
                                     deliveryInstructions, isDefault, fullAddress, createdAt);
        }
    }
}
