package com.wenl.backend.filter;

import com.wenl.backend.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SignatureException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Clear security context at the start of each request
        SecurityContextHolder.clearContext();

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtUtil.getUsernameFromToken(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Validate token BEFORE loading user to prevent unnecessary DB calls
                if (jwtUtil.validateTokenStructure(jwt)) { // New method to check signature/expiry

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    // Secondary validation with user details
                    if (jwtUtil.validateTokenAgainstUser(jwt, userDetails)) {

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("Authenticated user: {}", username);
                    }
                }
            }
        } catch (ExpiredJwtException ex) {
            logger.warn("JWT Token expired: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        } catch (SignatureException ex) {
            logger.warn("Invalid JWT signature: {}", ex.getMessage());
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature");
            return;
        } catch (Exception ex) {
            logger.error("Authentication error", ex);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
            return;
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.getWriter().write(message);
        response.getWriter().flush();
    }
}