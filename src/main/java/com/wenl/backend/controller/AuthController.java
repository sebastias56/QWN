package com.wenl.backend.controller;

import com.wenl.backend.model.User;
import com.wenl.backend.security.CustomUserDetails;
import com.wenl.backend.service.AuthService;
import com.wenl.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    // Registro de usuarios
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        authService.registerUser(user);
        return ResponseEntity.ok("Usuario registrado exitosamente");
    }

    // Inicio de sesión
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody CustomUserDetails user) {
        CustomUserDetails authenticatedUser = authService.authenticate(user.getUsername(), user.getPassword());
        String token = jwtUtil.generateToken(authenticatedUser);
        return ResponseEntity.ok(token);
    }

    // Recuperación de contraseña
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetEmail(email);
        return ResponseEntity.ok("Se ha enviado un correo para restablecer tu contraseña");
    }

    // Cerrar sesión (opcional)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.invalidateToken(token); // Invalidar el token JWT
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}