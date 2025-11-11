package com.cookedspecially.adminservice.dto.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleDTO {

    @NotBlank(message = "Role name is required")
    private String name;

    private String description;

    @JsonProperty("permission_ids")
    private Set<Long> permissionIds;
}
