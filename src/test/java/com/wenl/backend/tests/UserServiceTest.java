package com.wenl.backend.tests;

import com.wenl.backend.model.User;
import com.wenl.backend.repository.UserRepository;
import com.wenl.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class) // Use this instead of MockitoAnnotations.openMocks
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;  // Mock the password encoder

    @InjectMocks
    private UserService userService;

    @Test
    void testLoadUserByUsername_Success() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);
        mockUser.setPassword("encodedPassword"); // Important: Set the encoded password

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("nonexistentuser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.loadUserByUsername("nonexistentuser"));
    }

    @Test
    void testRegisterUser_Success() {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        User user = new User();
        user.setUsername("existinguser");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class)); // Added verification
    }
}