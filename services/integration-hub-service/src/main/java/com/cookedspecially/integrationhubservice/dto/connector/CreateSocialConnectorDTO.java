package com.cookedspecially.integrationhubservice.dto.connector;

import com.cookedspecially.integrationhubservice.domain.ConnectorType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSocialConnectorDTO {

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @NotNull(message = "Connector type is required")
    private ConnectorType type;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @JsonProperty("access_token")
    private String accessToken;

    private String configuration;
}
