package com.wenl.backend.service;

import com.wenl.backend.enums.Role;
import com.wenl.backend.model.User;
import com.wenl.backend.repository.UserRepository;
import com.wenl.backend.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    @Lazy
    public UserService( PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // Obtener todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener un usuario por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // Actualizar información del usuario
    public void updateUser(Long userId, String newEmail, String newUsername) {
        User user = getUserById(userId);

        if (newEmail != null && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("El correo electrónico ya está registrado");
            }
            user.setEmail(newEmail);
        }

        if (newUsername != null && !newUsername.equals(user.getUsername())) {
            user.setUsername(newUsername);
        }

        userRepository.save(user);
    }

    // Cambiar la contraseña del usuario
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        validatePassword(newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Cambiar el rol de un usuario
    public void changeUserRole(Long userId, Role newRole) {
        User user = getUserById(userId);
        user.setRole(newRole);
        userRepository.save(user);
    }

    // Cargar detalles del usuario por nombre de usuario
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new CustomUserDetails(user); // Devolver un CustomUserDetails
    }

    // Validar la contraseña
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres");
        }
    }
    public void registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}