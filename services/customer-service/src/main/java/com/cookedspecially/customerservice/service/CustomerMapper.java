package com.cookedspecially.customerservice.service;

import com.cookedspecially.customerservice.domain.Address;
import com.cookedspecially.customerservice.domain.Customer;
import com.cookedspecially.customerservice.domain.CustomerPreference;
import com.cookedspecially.customerservice.dto.AddressResponse;
import com.cookedspecially.customerservice.dto.CustomerPreferenceResponse;
import com.cookedspecially.customerservice.dto.CustomerResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper to convert between entities and DTOs
 */
@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerResponse.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .fullName(customer.getFullName())
                .dateOfBirth(customer.getDateOfBirth())
                .gender(customer.getGender())
                .profileImageUrl(customer.getProfileImageUrl())
                .status(customer.getStatus())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .accountCredit(customer.getAccountCredit())
                .emailNotifications(customer.getEmailNotifications())
                .smsNotifications(customer.getSmsNotifications())
                .pushNotifications(customer.getPushNotifications())
                .marketingEmails(customer.getMarketingEmails())
                .addresses(customer.getAddresses() != null ?
                        customer.getAddresses().stream()
                                .map(this::toAddressResponse)
                                .collect(Collectors.toList()) : null)
                .preferences(toPreferenceResponse(customer.getPreferences()))
                .totalOrders(customer.getTotalOrders())
                .totalSpent(customer.getTotalSpent())
                .lastOrderDate(customer.getLastOrderDate())
                .emailVerified(customer.getEmailVerified())
                .phoneVerified(customer.getPhoneVerified())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .label(address.getLabel())
                .streetAddress(address.getStreetAddress())
                .apartmentUnit(address.getApartmentUnit())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .deliveryInstructions(address.getDeliveryInstructions())
                .isDefault(address.getIsDefault())
                .fullAddress(address.getFullAddress())
                .createdAt(address.getCreatedAt())
                .build();
    }

    public CustomerPreferenceResponse toPreferenceResponse(CustomerPreference preference) {
        if (preference == null) {
            return null;
        }

        return CustomerPreferenceResponse.builder()
                .id(preference.getId())
                .dietaryRestrictions(preference.getDietaryRestrictions())
                .favoriteCuisines(preference.getFavoriteCuisines())
                .allergies(preference.getAllergies())
                .preferredPaymentMethod(preference.getPreferredPaymentMethod())
                .defaultTipPercentage(preference.getDefaultTipPercentage())
                .language(preference.getLanguage())
                .currency(preference.getCurrency())
                .darkMode(preference.getDarkMode())
                .showOnlineStatus(preference.getShowOnlineStatus())
                .shareLocation(preference.getShareLocation())
                .saveOrderHistory(preference.getSaveOrderHistory())
                .build();
    }
}
