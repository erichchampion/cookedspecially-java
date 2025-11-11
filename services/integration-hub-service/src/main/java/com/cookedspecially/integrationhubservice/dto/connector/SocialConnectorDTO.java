package com.cookedspecially.integrationhubservice.dto.connector;

import com.cookedspecially.integrationhubservice.domain.ConnectorStatus;
import com.cookedspecially.integrationhubservice.domain.ConnectorType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialConnectorDTO {

    private Long id;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @NotNull(message = "Connector type is required")
    private ConnectorType type;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    private ConnectorStatus status;

    @JsonProperty("last_sync_at")
    private LocalDateTime lastSyncAt;

    @JsonProperty("last_error")
    private String lastError;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
