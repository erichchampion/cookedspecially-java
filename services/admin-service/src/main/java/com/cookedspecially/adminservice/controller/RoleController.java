package com.cookedspecially.adminservice.controller;

import com.cookedspecially.adminservice.dto.role.CreateRoleDTO;
import com.cookedspecially.adminservice.dto.role.RoleDTO;
import com.cookedspecially.adminservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "Role and permission management endpoints")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role", description = "Create a new role")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody CreateRoleDTO dto) {
        RoleDTO created = roleService.createRole(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role", description = "Get role by ID")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        RoleDTO role = roleService.getRole(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    @Operation(summary = "List roles", description = "List all roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
