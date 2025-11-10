package com.cookedspecially.integrationhubservice.controller;

import com.cookedspecially.integrationhubservice.dto.connector.CreateSocialConnectorDTO;
import com.cookedspecially.integrationhubservice.dto.connector.SocialConnectorDTO;
import com.cookedspecially.integrationhubservice.dto.connector.UpdateSocialConnectorDTO;
import com.cookedspecially.integrationhubservice.service.SocialConnectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/integrations/social-connectors")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Social Connectors", description = "Social media connector management")
public class SocialConnectorController {

    private final SocialConnectorService service;

    @PostMapping
    @Operation(summary = "Create social connector", description = "Create a new social media connector")
    public ResponseEntity<SocialConnectorDTO> createConnector(@Valid @RequestBody CreateSocialConnectorDTO dto) {
        log.info("Creating social connector: type={}", dto.getType());
        SocialConnectorDTO created = service.createConnector(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update social connector", description = "Update an existing social media connector")
    public ResponseEntity<SocialConnectorDTO> updateConnector(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSocialConnectorDTO dto) {

        log.info("Updating social connector: id={}", id);
        SocialConnectorDTO updated = service.updateConnector(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get social connector", description = "Get social media connector by ID")
    public ResponseEntity<SocialConnectorDTO> getConnector(@PathVariable Long id) {
        log.info("Fetching social connector: id={}", id);
        SocialConnectorDTO connector = service.getConnector(id);
        return ResponseEntity.ok(connector);
    }

    @GetMapping
    @Operation(summary = "List social connectors", description = "List all social media connectors for a restaurant")
    public ResponseEntity<List<SocialConnectorDTO>> getConnectorsByRestaurant(
            @RequestParam Long restaurantId) {

        log.info("Fetching social connectors for restaurant: restaurantId={}", restaurantId);
        List<SocialConnectorDTO> connectors = service.getConnectorsByRestaurant(restaurantId);
        return ResponseEntity.ok(connectors);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete social connector", description = "Delete a social media connector")
    public ResponseEntity<Void> deleteConnector(@PathVariable Long id) {
        log.info("Deleting social connector: id={}", id);
        service.deleteConnector(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sync")
    @Operation(summary = "Sync social connector", description = "Trigger sync for social media connector")
    public ResponseEntity<Void> syncConnector(@PathVariable Long id) {
        log.info("Triggering sync for social connector: id={}", id);
        service.syncConnector(id);
        return ResponseEntity.ok().build();
    }
}
