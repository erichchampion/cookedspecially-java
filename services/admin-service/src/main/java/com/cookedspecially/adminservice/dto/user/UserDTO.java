package com.cookedspecially.adminservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String phone;

    @JsonProperty("cognito_sub")
    private String cognitoSub;

    private Boolean active;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("phone_verified")
    private Boolean phoneVerified;

    @JsonProperty("mfa_enabled")
    private Boolean mfaEnabled;

    @JsonProperty("last_login_at")
    private LocalDateTime lastLoginAt;

    private Set<String> roles;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
