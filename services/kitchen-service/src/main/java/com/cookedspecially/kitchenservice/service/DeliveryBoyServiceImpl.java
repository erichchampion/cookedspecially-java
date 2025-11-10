package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.DeliveryBoy;
import com.cookedspecially.kitchenservice.domain.DeliveryBoyStatus;
import com.cookedspecially.kitchenservice.repository.DeliveryBoyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for Delivery Boy operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeliveryBoyServiceImpl implements DeliveryBoyService {

    private final DeliveryBoyRepository deliveryBoyRepository;

    @Override
    public DeliveryBoy createDeliveryBoy(DeliveryBoy deliveryBoy) {
        log.info("Creating delivery boy: {}", deliveryBoy.getName());

        // Check if phone already exists
        if (deliveryBoyRepository.existsByPhoneAndDeletedAtIsNull(deliveryBoy.getPhone())) {
            throw new IllegalArgumentException("Delivery boy with phone '" + deliveryBoy.getPhone() + "' already exists");
        }

        deliveryBoy.setStatus(DeliveryBoyStatus.OFFLINE);
        deliveryBoy.setActive(true);
        deliveryBoy.setCurrentDeliveryCount(0);
        deliveryBoy.setTotalDeliveriesCompleted(0);
        deliveryBoy.setAverageRating(0.0);
        deliveryBoy.setRatingCount(0);

        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy updateDeliveryBoy(Long deliveryBoyId, DeliveryBoy updatedDeliveryBoy) {
        log.info("Updating delivery boy: {}", deliveryBoyId);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);

        deliveryBoy.setName(updatedDeliveryBoy.getName());
        deliveryBoy.setEmail(updatedDeliveryBoy.getEmail());
        deliveryBoy.setVehicleType(updatedDeliveryBoy.getVehicleType());
        deliveryBoy.setVehicleNumber(updatedDeliveryBoy.getVehicleNumber());
        deliveryBoy.setLicenseNumber(updatedDeliveryBoy.getLicenseNumber());
        deliveryBoy.setNotes(updatedDeliveryBoy.getNotes());
        deliveryBoy.setUpdatedBy(updatedDeliveryBoy.getUpdatedBy());

        // Phone update requires uniqueness check
        if (!deliveryBoy.getPhone().equals(updatedDeliveryBoy.getPhone())) {
            if (deliveryBoyRepository.existsByPhoneAndDeletedAtIsNull(updatedDeliveryBoy.getPhone())) {
                throw new IllegalArgumentException("Phone number already in use");
            }
            deliveryBoy.setPhone(updatedDeliveryBoy.getPhone());
        }

        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryBoy getDeliveryBoyById(Long deliveryBoyId) {
        return deliveryBoyRepository.findById(deliveryBoyId)
            .orElseThrow(() -> new IllegalArgumentException("Delivery boy not found with ID: " + deliveryBoyId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryBoy> getDeliveryBoysByRestaurant(Long restaurantId) {
        return deliveryBoyRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryBoy> getActiveDeliveryBoys(Long restaurantId) {
        return deliveryBoyRepository.findActiveDeliveryBoys(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryBoy> getAvailableDeliveryBoys(Long restaurantId) {
        return deliveryBoyRepository.findAvailableDeliveryBoys(restaurantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryBoy> getDeliveryBoysByStatus(DeliveryBoyStatus status) {
        return deliveryBoyRepository.findByStatusAndDeletedAtIsNull(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryBoy> getTopRatedDeliveryBoys(Long restaurantId) {
        return deliveryBoyRepository.findTopRatedDeliveryBoys(restaurantId);
    }

    @Override
    public DeliveryBoy assignDelivery(Long deliveryBoyId) {
        log.info("Assigning delivery to delivery boy: {}", deliveryBoyId);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);

        if (!deliveryBoy.isAvailable()) {
            throw new IllegalStateException("Delivery boy is not available for assignment");
        }

        deliveryBoy.assignDelivery();
        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy completeDelivery(Long deliveryBoyId) {
        log.info("Completing delivery for delivery boy: {}", deliveryBoyId);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);

        if (deliveryBoy.getStatus() != DeliveryBoyStatus.ON_DELIVERY) {
            throw new IllegalStateException("Delivery boy is not on delivery");
        }

        deliveryBoy.completeDelivery();
        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy updateStatus(Long deliveryBoyId, DeliveryBoyStatus status) {
        log.info("Updating delivery boy {} status to: {}", deliveryBoyId, status);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);
        deliveryBoy.setStatus(status);

        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy updateRating(Long deliveryBoyId, double rating) {
        log.info("Updating delivery boy {} rating: {}", deliveryBoyId, rating);

        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);
        deliveryBoy.updateRating(rating);

        return deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy markAvailable(Long deliveryBoyId) {
        return updateStatus(deliveryBoyId, DeliveryBoyStatus.AVAILABLE);
    }

    @Override
    public DeliveryBoy markOnBreak(Long deliveryBoyId) {
        return updateStatus(deliveryBoyId, DeliveryBoyStatus.BREAK);
    }

    @Override
    public DeliveryBoy markOffline(Long deliveryBoyId) {
        return updateStatus(deliveryBoyId, DeliveryBoyStatus.OFFLINE);
    }

    @Override
    public void deleteDeliveryBoy(Long deliveryBoyId) {
        log.info("Deleting delivery boy: {}", deliveryBoyId);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);

        if (deliveryBoy.getCurrentDeliveryCount() > 0) {
            throw new IllegalStateException("Cannot delete delivery boy with active deliveries");
        }

        deliveryBoy.delete();
        deliveryBoyRepository.save(deliveryBoy);
    }

    @Override
    public DeliveryBoy restoreDeliveryBoy(Long deliveryBoyId) {
        log.info("Restoring delivery boy: {}", deliveryBoyId);

        DeliveryBoy deliveryBoy = getDeliveryBoyById(deliveryBoyId);

        if (deliveryBoy.getDeletedAt() == null) {
            throw new IllegalStateException("Delivery boy is not deleted");
        }

        deliveryBoy.setActive(true);
        deliveryBoy.setDeletedAt(null);
        deliveryBoy.setStatus(DeliveryBoyStatus.OFFLINE);

        return deliveryBoyRepository.save(deliveryBoy);
    }
}
