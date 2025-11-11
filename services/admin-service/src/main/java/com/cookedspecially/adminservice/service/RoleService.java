package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.Permission;
import com.cookedspecially.adminservice.domain.Role;
import com.cookedspecially.adminservice.dto.role.CreateRoleDTO;
import com.cookedspecially.adminservice.dto.role.RoleDTO;
import com.cookedspecially.adminservice.repository.PermissionRepository;
import com.cookedspecially.adminservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public RoleDTO createRole(CreateRoleDTO dto) {
        log.info("Creating role: name={}", dto.getName());

        if (roleRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .active(true)
                .permissions(new HashSet<>())
                .build();

        if (dto.getPermissionIds() != null && !dto.getPermissionIds().isEmpty()) {
            Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(dto.getPermissionIds()));
            role.setPermissions(permissions);
        }

        Role saved = roleRepository.save(role);
        log.info("Role created: id={}", saved.getId());

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public RoleDTO getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found: " + id));
        return mapToDTO(role);
    }

    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role: id={}", id);
        roleRepository.deleteById(id);
    }

    private RoleDTO mapToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.getActive())
                .permissionIds(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toSet()))
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
