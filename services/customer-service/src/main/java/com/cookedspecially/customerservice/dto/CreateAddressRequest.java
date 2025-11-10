package com.cookedspecially.customerservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Create address request DTO
 */
public class CreateAddressRequest {

    private String addressType;  // HOME, WORK, OTHER

    @Size(max = 100, message = "Label must not exceed 100 characters")
    private String label;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must not exceed 255 characters")
    private String streetAddress;

    @Size(max = 50, message = "Apartment/unit must not exceed 50 characters")
    private String apartmentUnit;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @Size(max = 500, message = "Delivery instructions must not exceed 500 characters")
    private String deliveryInstructions;

    private Boolean isDefault;

    // Constructors
    public CreateAddressRequest() {
    }

    public CreateAddressRequest(String addressType, String label, String streetAddress,
                                String apartmentUnit, String city, String state,
                                String postalCode, String country, BigDecimal latitude,
                                BigDecimal longitude, String deliveryInstructions,
                                Boolean isDefault) {
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
    }

    // Getters and Setters
    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getApartmentUnit() {
        return apartmentUnit;
    }

    public void setApartmentUnit(String apartmentUnit) {
        this.apartmentUnit = apartmentUnit;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getDeliveryInstructions() {
        return deliveryInstructions;
    }

    public void setDeliveryInstructions(String deliveryInstructions) {
        this.deliveryInstructions = deliveryInstructions;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateAddressRequest that = (CreateAddressRequest) o;
        return Objects.equals(addressType, that.addressType) &&
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
                Objects.equals(isDefault, that.isDefault);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressType, label, streetAddress, apartmentUnit, city,
                state, postalCode, country, latitude, longitude, deliveryInstructions,
                isDefault);
    }

    // toString
    @Override
    public String toString() {
        return "CreateAddressRequest{" +
                "addressType='" + addressType + '\'' +
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
                '}';
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
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

        public CreateAddressRequest build() {
            return new CreateAddressRequest(addressType, label, streetAddress,
                    apartmentUnit, city, state, postalCode, country, latitude,
                    longitude, deliveryInstructions, isDefault);
        }
    }
}
