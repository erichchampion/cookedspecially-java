package com.cookedspecially.integrationhubservice.service;

import com.cookedspecially.integrationhubservice.domain.ConnectorStatus;
import com.cookedspecially.integrationhubservice.domain.SocialConnector;
import com.cookedspecially.integrationhubservice.dto.connector.CreateSocialConnectorDTO;
import com.cookedspecially.integrationhubservice.dto.connector.SocialConnectorDTO;
import com.cookedspecially.integrationhubservice.dto.connector.UpdateSocialConnectorDTO;
import com.cookedspecially.integrationhubservice.repository.SocialConnectorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialConnectorService {

    private final SocialConnectorRepository repository;

    @Transactional
    public SocialConnectorDTO createConnector(CreateSocialConnectorDTO dto) {
        log.info("Creating social connector: type={}, restaurantId={}", dto.getType(), dto.getRestaurantId());

        SocialConnector connector = SocialConnector.builder()
                .restaurantId(dto.getRestaurantId())
                .type(dto.getType())
                .name(dto.getName())
                .description(dto.getDescription())
                .accessToken(dto.getAccessToken())
                .configuration(dto.getConfiguration())
                .enabled(true)
                .status(ConnectorStatus.INACTIVE)
                .build();

        SocialConnector saved = repository.save(connector);
        log.info("Social connector created successfully: id={}", saved.getId());

        return mapToDTO(saved);
    }

    @Transactional
    public SocialConnectorDTO updateConnector(Long id, UpdateSocialConnectorDTO dto) {
        log.info("Updating social connector: id={}", id);

        SocialConnector connector = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social connector not found: " + id));

        if (dto.getName() != null) {
            connector.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            connector.setDescription(dto.getDescription());
        }
        if (dto.getEnabled() != null) {
            connector.setEnabled(dto.getEnabled());
        }
        if (dto.getAccessToken() != null) {
            connector.setAccessToken(dto.getAccessToken());
            connector.setStatus(ConnectorStatus.ACTIVE);
        }
        if (dto.getConfiguration() != null) {
            connector.setConfiguration(dto.getConfiguration());
        }

        SocialConnector updated = repository.save(connector);
        log.info("Social connector updated successfully: id={}", id);

        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public SocialConnectorDTO getConnector(Long id) {
        log.debug("Fetching social connector: id={}", id);

        SocialConnector connector = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social connector not found: " + id));

        return mapToDTO(connector);
    }

    @Transactional(readOnly = true)
    public List<SocialConnectorDTO> getConnectorsByRestaurant(Long restaurantId) {
        log.debug("Fetching social connectors for restaurant: restaurantId={}", restaurantId);

        return repository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteConnector(Long id) {
        log.info("Deleting social connector: id={}", id);

        if (!repository.existsById(id)) {
            throw new RuntimeException("Social connector not found: " + id);
        }

        repository.deleteById(id);
        log.info("Social connector deleted successfully: id={}", id);
    }

    @Transactional
    public void syncConnector(Long id) {
        log.info("Triggering sync for social connector: id={}", id);

        SocialConnector connector = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Social connector not found: " + id));

        // TODO: Implement actual sync logic based on connector type
        connector.setLastSyncAt(LocalDateTime.now());
        connector.setStatus(ConnectorStatus.ACTIVE);

        repository.save(connector);
        log.info("Social connector sync completed: id={}", id);
    }

    private SocialConnectorDTO mapToDTO(SocialConnector connector) {
        return SocialConnectorDTO.builder()
                .id(connector.getId())
                .restaurantId(connector.getRestaurantId())
                .type(connector.getType())
                .name(connector.getName())
                .description(connector.getDescription())
                .enabled(connector.getEnabled())
                .status(connector.getStatus())
                .lastSyncAt(connector.getLastSyncAt())
                .lastError(connector.getLastError())
                .createdAt(connector.getCreatedAt())
                .updatedAt(connector.getUpdatedAt())
                .build();
    }
}
