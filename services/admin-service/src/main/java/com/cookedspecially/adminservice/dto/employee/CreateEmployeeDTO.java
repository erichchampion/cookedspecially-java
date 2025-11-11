package com.cookedspecially.adminservice.dto.employee;

import com.cookedspecially.adminservice.domain.EmploymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeDTO {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("employee_code")
    private String employeeCode;

    private String designation;
    private String department;

    @JsonProperty("joining_date")
    private LocalDate joiningDate;

    @JsonProperty("employment_type")
    private EmploymentType employmentType;

    private BigDecimal salary;
    private String address;

    @JsonProperty("emergency_contact")
    private String emergencyContact;

    @JsonProperty("emergency_contact_name")
    private String emergencyContactName;

    private String notes;
}
