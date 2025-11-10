package com.cookedspecially.adminservice.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Integer expiresIn;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("mfa_required")
    private Boolean mfaRequired;

    @JsonProperty("user_id")
    private Long userId;

    private String username;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
