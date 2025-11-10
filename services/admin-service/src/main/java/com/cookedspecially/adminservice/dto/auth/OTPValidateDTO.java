package com.cookedspecially.adminservice.dto.auth;

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
public class OTPValidateDTO {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotBlank(message = "OTP code is required")
    private String code;
}
