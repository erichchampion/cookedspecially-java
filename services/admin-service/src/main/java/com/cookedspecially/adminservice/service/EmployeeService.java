package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.Employee;
import com.cookedspecially.adminservice.dto.employee.CreateEmployeeDTO;
import com.cookedspecially.adminservice.dto.employee.EmployeeDTO;
import com.cookedspecially.adminservice.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Transactional
    public EmployeeDTO createEmployee(CreateEmployeeDTO dto) {
        log.info("Creating employee for user: userId={}", dto.getUserId());

        Employee employee = Employee.builder()
                .userId(dto.getUserId())
                .restaurantId(dto.getRestaurantId())
                .employeeCode(dto.getEmployeeCode())
                .designation(dto.getDesignation())
                .department(dto.getDepartment())
                .joiningDate(dto.getJoiningDate())
                .employmentType(dto.getEmploymentType())
                .salary(dto.getSalary())
                .address(dto.getAddress())
                .emergencyContact(dto.getEmergencyContact())
                .emergencyContactName(dto.getEmergencyContactName())
                .notes(dto.getNotes())
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created: id={}", saved.getId());

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeDTO getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + id));
        return mapToDTO(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByRestaurant(Long restaurantId) {
        return employeeRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee: id={}", id);
        employeeRepository.deleteById(id);
    }

    private EmployeeDTO mapToDTO(Employee employee) {
        return EmployeeDTO.builder()
                .id(employee.getId())
                .userId(employee.getUserId())
                .restaurantId(employee.getRestaurantId())
                .employeeCode(employee.getEmployeeCode())
                .designation(employee.getDesignation())
                .department(employee.getDepartment())
                .joiningDate(employee.getJoiningDate())
                .terminationDate(employee.getTerminationDate())
                .status(employee.getStatus())
                .employmentType(employee.getEmploymentType())
                .salary(employee.getSalary())
                .address(employee.getAddress())
                .emergencyContact(employee.getEmergencyContact())
                .emergencyContactName(employee.getEmergencyContactName())
                .notes(employee.getNotes())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
