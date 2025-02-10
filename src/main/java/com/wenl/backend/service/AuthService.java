package com.wenl.backend.service;

import com.wenl.backend.enums.Role;
import com.wenl.backend.model.User;
import com.wenl.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Set<String> blacklistedTokens = new HashSet<>(); // Lista negra de tokens

    // Registro de usuarios
    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }
        validatePassword(user.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER); // Asignar rol por defecto
        userRepository.save(user);
    }

    // Autenticación de usuarios
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        return user;
    }

    // Invalidar un token JWT
    public void invalidateToken(String token) {
        blacklistedTokens.add(token); // Agregar el token a la lista negra
    }

    public void sendPasswordResetEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // Simulación de envío de correo
        System.out.println("Correo de restablecimiento enviado a: " + email);
    }

    // Verificar si un token está en la lista negra
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    // Validar la contraseña
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
    }
}