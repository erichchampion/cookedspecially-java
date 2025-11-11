package com.cookedspecially.adminservice.dto.employee;

import com.cookedspecially.adminservice.domain.EmploymentStatus;
import com.cookedspecially.adminservice.domain.EmploymentType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("restaurant_id")
    private Long restaurantId;

    @JsonProperty("employee_code")
    private String employeeCode;

    private String designation;
    private String department;

    @JsonProperty("joining_date")
    private LocalDate joiningDate;

    @JsonProperty("termination_date")
    private LocalDate terminationDate;

    private EmploymentStatus status;

    @JsonProperty("employment_type")
    private EmploymentType employmentType;

    private BigDecimal salary;
    private String address;

    @JsonProperty("emergency_contact")
    private String emergencyContact;

    @JsonProperty("emergency_contact_name")
    private String emergencyContactName;

    private String notes;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
