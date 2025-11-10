package com.cookedspecially.adminservice.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
    @Index(name = "idx_employees_user_id", columnList = "userId"),
    @Index(name = "idx_employees_restaurant_id", columnList = "restaurantId"),
    @Index(name = "idx_employees_employee_code", columnList = "employeeCode")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long restaurantId;

    @Column(unique = true, length = 50)
    private String employeeCode;

    @Column(length = 100)
    private String designation;

    @Column(length = 50)
    private String department;

    @Column
    private LocalDate joiningDate;

    @Column
    private LocalDate terminationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmploymentType employmentType;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String emergencyContact;

    @Column(length = 100)
    private String emergencyContactName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = EmploymentStatus.ACTIVE;
        }
        if (employmentType == null) {
            employmentType = EmploymentType.FULL_TIME;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
