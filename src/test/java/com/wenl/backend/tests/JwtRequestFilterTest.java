package com.wenl.backend.tests;

import com.wenl.backend.filter.JwtRequestFilter;
import com.wenl.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtRequestFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private JwtRequestFilter jwtRequestFilter;

    @Mock
    private FilterChain filterChain;

    @Test
    void testDoFilterInternal_InvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid_token");

        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractUsername(anyString())).thenReturn(null);

        PrintWriter writer = new PrintWriter(response.getWriter());
        when(response.getWriter()).thenReturn(writer);

        jwtRequestFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(403);
        String errorMessage = response.getContentAsString();
        assertTrue(errorMessage.contains("Unauthorized"));

    }

    // ... otros tests (si los tienes)
}