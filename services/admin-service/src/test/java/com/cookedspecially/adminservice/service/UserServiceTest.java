package com.cookedspecially.adminservice.service;

import com.cookedspecially.adminservice.domain.User;
import com.cookedspecially.adminservice.dto.user.CreateUserDTO;
import com.cookedspecially.adminservice.dto.user.UserDTO;
import com.cookedspecially.adminservice.repository.RoleRepository;
import com.cookedspecially.adminservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateUserDTO createUserDTO;
    private User user;

    @BeforeEach
    void setUp() {
        createUserDTO = CreateUserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .firstName("Test")
                .lastName("User")
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .roles(new HashSet<>())
                .build();
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.createUser(createUserDTO);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByUsername(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.createUser(createUserDTO));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testGetUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUser(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void testGetUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUser(1L));
    }
}
