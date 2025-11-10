package com.cookedspecially.customerservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer address entity
 */
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_address_type", columnList = "address_type")
})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Column(name = "address_type", length = 20)
    private String addressType = "HOME";  // HOME, WORK, OTHER

    @Column(name = "address_label", length = 100)
    private String label;  // e.g., "Home", "Office", "Mom's house"

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "apartment_unit", length = 50)
    private String apartmentUnit;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(nullable = false, length = 100)
    private String country;

    // Geocoding
    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    // Delivery instructions
    @Column(name = "delivery_instructions", length = 500)
    private String deliveryInstructions;

    // Default address flag
    @Column(name = "is_default")
    private Boolean isDefault = false;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Address() {
        this.addressType = "HOME";
        this.isDefault = false;
    }

    public Address(Long id, Customer customer, String addressType, String label, String streetAddress,
                   String apartmentUnit, String city, String state, String postalCode, String country,
                   BigDecimal latitude, BigDecimal longitude, String deliveryInstructions, Boolean isDefault,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
        this.addressType = addressType != null ? addressType : "HOME";
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
        this.isDefault = isDefault != null ? isDefault : false;
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
        Address address = (Address) o;
        return Objects.equals(id, address.id) &&
                Objects.equals(customer, address.customer) &&
                Objects.equals(addressType, address.addressType) &&
                Objects.equals(label, address.label) &&
                Objects.equals(streetAddress, address.streetAddress) &&
                Objects.equals(apartmentUnit, address.apartmentUnit) &&
                Objects.equals(city, address.city) &&
                Objects.equals(state, address.state) &&
                Objects.equals(postalCode, address.postalCode) &&
                Objects.equals(country, address.country) &&
                Objects.equals(latitude, address.latitude) &&
                Objects.equals(longitude, address.longitude) &&
                Objects.equals(deliveryInstructions, address.deliveryInstructions) &&
                Objects.equals(isDefault, address.isDefault) &&
                Objects.equals(createdAt, address.createdAt) &&
                Objects.equals(updatedAt, address.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, addressType, label, streetAddress, apartmentUnit,
                city, state, postalCode, country, latitude, longitude, deliveryInstructions,
                isDefault, createdAt, updatedAt);
    }

    // toString
    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", customer=" + customer +
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
        private String addressType = "HOME";
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
        private Boolean isDefault = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder customer(Customer customer) {
            this.customer = customer;
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Address build() {
            return new Address(id, customer, addressType, label, streetAddress, apartmentUnit,
                    city, state, postalCode, country, latitude, longitude, deliveryInstructions,
                    isDefault, createdAt, updatedAt);
        }
    }

    // Helper methods
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetAddress);
        if (apartmentUnit != null && !apartmentUnit.isEmpty()) {
            sb.append(", ").append(apartmentUnit);
        }
        sb.append(", ").append(city);
        if (state != null && !state.isEmpty()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isEmpty()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }
}
