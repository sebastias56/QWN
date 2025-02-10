package com.wenl.backend.tests;

import com.wenl.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testBeansAreLoaded() {
        assertNotNull(userService, "UserService should be loaded");
        assertNotNull(passwordEncoder, "PasswordEncoder should be loaded");
    }
}