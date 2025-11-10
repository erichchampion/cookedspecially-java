package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.DeliveryArea;
import com.cookedspecially.kitchenservice.repository.DeliveryAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation for Delivery Area operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryAreaServiceImpl implements DeliveryAreaService {

    private final DeliveryAreaRepository deliveryAreaRepository;

    @Override
    public DeliveryArea createDeliveryArea(DeliveryArea deliveryArea) {
        log.info("Creating delivery area: {}", deliveryArea.getName());

        // Check if area with same name exists for restaurant
        if (deliveryAreaRepository.existsByRestaurantIdAndNameAndDeletedAtIsNull(
                deliveryArea.getRestaurantId(), deliveryArea.getName())) {
            throw new IllegalArgumentException("Delivery area with name '" + deliveryArea.getName() +
                "' already exists for this restaurant");
        }

        deliveryArea.setActive(true);
        return deliveryAreaRepository.save(deliveryArea);
    }

    @Override
    public DeliveryArea updateDeliveryArea(Long areaId, DeliveryArea updatedArea) {
        log.info("Updating delivery area: {}", areaId);

        DeliveryArea area = getDeliveryAreaById(areaId);

        // Check name uniqueness if changed
        if (!area.getName().equals(updatedArea.getName())) {
            if (deliveryAreaRepository.existsByRestaurantIdAndNameAndDeletedAtIsNull(
                    area.getRestaurantId(), updatedArea.getName())) {
                throw new IllegalArgumentException("Delivery area with name '" + updatedArea.getName() +
                    "' already exists");
            }
        }

        area.setName(updatedArea.getName());
        area.setDescription(updatedArea.getDescription());
        area.setDeliveryCharge(updatedArea.getDeliveryCharge());
        area.setMinimumOrderAmount(updatedArea.getMinimumOrderAmount());
        area.setFreeDeliveryAbove(updatedArea.getFreeDeliveryAbove());
        area.setEstimatedDeliveryTime(updatedArea.getEstimatedDeliveryTime());
        area.setZipCode(updatedArea.getZipCode());
        area.setCity(updatedArea.getCity());
        area.setState(updatedArea.getState());
        area.setCountry(updatedArea.getCountry());
        area.setDisplayOrder(updatedArea.getDisplayOrder());
        area.setUpdatedBy(updatedArea.getUpdatedBy());

        return deliveryAreaRepository.save(area);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryArea getDeliveryAreaById(Long areaId) {
        return deliveryAreaRepository.findById(areaId)
            .orElseThrow(() -> new IllegalArgumentException("Delivery area not found with ID: " + areaId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryArea> getDeliveryAreasByRestaurant(Long restaurantId) {
        return deliveryAreaRepository.findByRestaurantIdAndDeletedAtIsNullOrderByDisplayOrderAsc(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryArea> getActiveDeliveryAreas(Long restaurantId) {
        return deliveryAreaRepository.findActiveDeliveryAreas(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryArea> findDeliveryAreasByZipCode(Long restaurantId, String zipCode) {
        return deliveryAreaRepository.findByRestaurantIdAndZipCodeAndDeletedAtIsNull(restaurantId, zipCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryArea> findDeliveryAreasByCity(Long restaurantId, String city) {
        return deliveryAreaRepository.findByRestaurantIdAndCityAndDeletedAtIsNull(restaurantId, city);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDeliveryCharge(Long areaId, BigDecimal orderAmount) {
        DeliveryArea area = getDeliveryAreaById(areaId);
        return area.calculateDeliveryCharge(orderAmount);
    }

    @Override
    public void deleteDeliveryArea(Long areaId) {
        log.info("Deleting delivery area: {}", areaId);

        DeliveryArea area = getDeliveryAreaById(areaId);
        area.delete();
        deliveryAreaRepository.save(area);
    }

    @Override
    public DeliveryArea restoreDeliveryArea(Long areaId) {
        log.info("Restoring delivery area: {}", areaId);

        DeliveryArea area = getDeliveryAreaById(areaId);

        if (area.getDeletedAt() == null) {
            throw new IllegalStateException("Delivery area is not deleted");
        }

        area.setActive(true);
        area.setDeletedAt(null);

        return deliveryAreaRepository.save(area);
    }
}
