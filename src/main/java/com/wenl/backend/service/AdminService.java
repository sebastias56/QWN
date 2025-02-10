package com.wenl.backend.service;

import com.wenl.backend.enums.Role;
import com.wenl.backend.model.User;
import com.wenl.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Cambiar el rol de un usuario
    public void changeUserRole(Long userId, String role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setRole(Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
    }
}