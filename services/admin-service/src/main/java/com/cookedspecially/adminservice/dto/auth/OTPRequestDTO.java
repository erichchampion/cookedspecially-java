package com.cookedspecially.adminservice.dto.auth;

import com.cookedspecially.adminservice.domain.OTPType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OTPRequestDTO {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "OTP type is required")
    private OTPType type;
}
