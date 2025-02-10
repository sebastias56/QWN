package com.wenl.backend.repository;

import com.wenl.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // Buscar usuario por correo electrónico

    Optional<User> findByUsername(String username); // Buscar usuario por nombre de usuario
    boolean existsByUsername(String username);
    boolean existsByEmail(String email); // Verificar si un correo electrónico ya está registrado
}