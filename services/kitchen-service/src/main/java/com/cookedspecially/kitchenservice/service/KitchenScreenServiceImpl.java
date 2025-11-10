package com.cookedspecially.kitchenservice.service;

import com.cookedspecially.kitchenservice.domain.KitchenScreen;
import com.cookedspecially.kitchenservice.domain.KitchenScreenStatus;
import com.cookedspecially.kitchenservice.repository.KitchenScreenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service implementation for Kitchen Screen operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class KitchenScreenServiceImpl implements KitchenScreenService {

    private final KitchenScreenRepository screenRepository;

    @Override
    public KitchenScreen createScreen(KitchenScreen screen) {
        log.info("Creating kitchen screen: {}", screen.getName());

        // Check if screen with same name exists for fulfillment center
        if (screenRepository.existsByNameAndFulfillmentCenterId(screen.getName(), screen.getFulfillmentCenterId())) {
            throw new IllegalArgumentException("Screen with name '" + screen.getName() + "' already exists for this fulfillment center");
        }

        screen.setStatus(KitchenScreenStatus.INACTIVE);
        return screenRepository.save(screen);
    }

    @Override
    public KitchenScreen updateScreen(Long screenId, KitchenScreen updatedScreen) {
        log.info("Updating kitchen screen: {}", screenId);

        KitchenScreen screen = getScreenById(screenId);

        screen.setName(updatedScreen.getName());
        screen.setDescription(updatedScreen.getDescription());
        screen.setStationType(updatedScreen.getStationType());
        screen.setSoundEnabled(updatedScreen.getSoundEnabled());
        screen.setAutoAcceptOrders(updatedScreen.getAutoAcceptOrders());
        screen.setDisplayOrder(updatedScreen.getDisplayOrder());
        screen.setUpdatedBy(updatedScreen.getUpdatedBy());

        return screenRepository.save(screen);
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenScreen getScreenById(Long screenId) {
        return screenRepository.findById(screenId)
            .orElseThrow(() -> new IllegalArgumentException("Kitchen screen not found with ID: " + screenId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenScreen> getScreensByFulfillmentCenter(Long fulfillmentCenterId) {
        return screenRepository.findByFulfillmentCenterIdOrderByDisplayOrderAsc(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenScreen> getActiveScreens(Long fulfillmentCenterId) {
        return screenRepository.findActiveScreensByFulfillmentCenter(fulfillmentCenterId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenScreen> getScreensByStatus(KitchenScreenStatus status) {
        return screenRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenScreen getScreenByDeviceId(String deviceId) {
        return screenRepository.findByDeviceId(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Kitchen screen not found with device ID: " + deviceId));
    }

    @Override
    public KitchenScreen updateHeartbeat(Long screenId) {
        log.debug("Updating heartbeat for screen: {}", screenId);

        KitchenScreen screen = getScreenById(screenId);
        screen.updateHeartbeat();

        return screenRepository.save(screen);
    }

    @Override
    public KitchenScreen markOffline(Long screenId) {
        log.info("Marking screen {} as offline", screenId);

        KitchenScreen screen = getScreenById(screenId);
        screen.markOffline();

        return screenRepository.save(screen);
    }

    @Override
    public KitchenScreen markActive(Long screenId) {
        log.info("Marking screen {} as active", screenId);

        KitchenScreen screen = getScreenById(screenId);
        screen.setStatus(KitchenScreenStatus.ACTIVE);

        return screenRepository.save(screen);
    }

    @Override
    public KitchenScreen markMaintenance(Long screenId) {
        log.info("Marking screen {} as maintenance", screenId);

        KitchenScreen screen = getScreenById(screenId);
        screen.setStatus(KitchenScreenStatus.MAINTENANCE);

        return screenRepository.save(screen);
    }

    @Override
    public void checkAndMarkOfflineScreens() {
        log.debug("Checking for offline screens");

        // Find screens with stale heartbeat (> 2 minutes)
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);
        List<KitchenScreen> staleScreens = screenRepository.findScreensWithStaleHeartbeat(threshold);

        for (KitchenScreen screen : staleScreens) {
            log.warn("Marking screen {} as offline due to stale heartbeat", screen.getId());
            screen.markOffline();
            screenRepository.save(screen);
        }
    }

    @Override
    public void deleteScreen(Long screenId) {
        log.info("Deleting kitchen screen: {}", screenId);

        KitchenScreen screen = getScreenById(screenId);

        if (screen.getStatus() == KitchenScreenStatus.ACTIVE) {
            throw new IllegalStateException("Cannot delete an active screen");
        }

        screenRepository.delete(screen);
    }
}
