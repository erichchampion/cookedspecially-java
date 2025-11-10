package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.Role;
import com.cookedspecially.adminservice.domain.User;
import com.cookedspecially.adminservice.dto.user.CreateUserDTO;
import com.cookedspecially.adminservice.dto.user.UpdateUserDTO;
import com.cookedspecially.adminservice.dto.user.UserDTO;
import com.cookedspecially.adminservice.repository.RoleRepository;
import com.cookedspecially.adminservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(CreateUserDTO dto) {
        log.info("Creating user: username={}", dto.getUsername());

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .phone(dto.getPhone())
                .active(true)
                .emailVerified(false)
                .phoneVerified(false)
                .mfaEnabled(false)
                .failedLoginAttempts(0)
                .roles(new HashSet<>())
                .build();

        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);
        log.info("User created successfully: id={}", saved.getId());

        return mapToDTO(saved);
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserDTO dto) {
        log.info("Updating user: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getActive() != null) user.setActive(dto.getActive());
        if (dto.getMfaEnabled() != null) user.setMfaEnabled(dto.getMfaEnabled());

        if (dto.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(dto.getRoleIds()));
            user.setRoles(roles);
        }

        User updated = userRepository.save(user);
        log.info("User updated successfully: id={}", id);

        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: id={}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .cognitoSub(user.getCognitoSub())
                .active(user.getActive())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .mfaEnabled(user.getMfaEnabled())
                .lastLoginAt(user.getLastLoginAt())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
