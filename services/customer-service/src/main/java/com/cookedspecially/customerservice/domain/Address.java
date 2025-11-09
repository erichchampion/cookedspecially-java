package com.cookedspecially.customerservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Customer address entity
 */
@Entity
@Table(name = "addresses", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_address_type", columnList = "address_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    private Customer customer;

    @Column(name = "address_type", length = 20)
    @Builder.Default
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
    @Builder.Default
    private Boolean isDefault = false;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
