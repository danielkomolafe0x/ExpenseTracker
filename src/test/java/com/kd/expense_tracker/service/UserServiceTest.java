package com.kd.expense_tracker.service;

import com.kd.expense_tracker.model.Role;
import com.kd.expense_tracker.model.User;
import com.kd.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_savesNewUserWithHashedPassword() {
        // Arrange
        //noinspection SpellCheckingInspection
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plaintext123")).thenReturn("hashed_value");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        @SuppressWarnings("SpellCheckingInspection") User result = userService.registerUser("alice", "alice@example.com", "plaintext123");

        // Assert
        //noinspection SpellCheckingInspection
        assertEquals("alice", result.getUsername());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("hashed_value", result.getPassword());
        assertEquals(Role.USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_throwsWhenUsernameAlreadyExists() {
        // Arrange
        //noinspection SpellCheckingInspection
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        // Act + Assert
        @SuppressWarnings("SpellCheckingInspection") IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("alice", "alice@example.com", "plaintext123")
        );
        assertEquals("Username already taken", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_throwsWhenEmailAlreadyExists() {
        // Arrange
        //noinspection SpellCheckingInspection
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // Act + Assert
        @SuppressWarnings("SpellCheckingInspection") IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("alice", "alice@example.com", "plaintext123")
        );
        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }
}