package com.wenl.backend.tests;

import com.wenl.backend.filter.JwtRequestFilter;
import com.wenl.backend.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.security.SignatureException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Add these annotations
@ExtendWith(MockitoExtension.class)
class JwtRequestFilterTest {

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        // Arrange
        String invalidToken = "invalid.token.here";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtil.getUsernameFromToken(invalidToken)).thenThrow(new SignatureException("Invalid token"));

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        // Arrange
        String validToken = "valid.token.here";
        String username = "user@example.com";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                username,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtil.getUsernameFromToken(validToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(validToken, userDetails.getUsername())).thenReturn(true);

        // Clear previous authentication
        SecurityContextHolder.clearContext();

        // Act
        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify the request continues down the filter chain
        verify(filterChain).doFilter(request, response);

        // Check authentication was set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());

        // Verify authorities are properly set
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        assertTrue(authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        // Ensure no errors were sent
        verify(response, never()).sendError(anyInt(), anyString());
    }
}