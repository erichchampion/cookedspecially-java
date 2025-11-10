package com.cookedspecially.adminservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {

    @Email(message = "Invalid email format")
    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String phone;

    private Boolean active;

    @JsonProperty("mfa_enabled")
    private Boolean mfaEnabled;

    @JsonProperty("role_ids")
    private Set<Long> roleIds;
}
