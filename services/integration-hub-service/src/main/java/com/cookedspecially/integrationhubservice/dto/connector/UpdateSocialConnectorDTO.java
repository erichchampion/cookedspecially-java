package com.cookedspecially.integrationhubservice.dto.connector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSocialConnectorDTO {

    private String name;

    private String description;

    private Boolean enabled;

    @JsonProperty("access_token")
    private String accessToken;

    private String configuration;
}
