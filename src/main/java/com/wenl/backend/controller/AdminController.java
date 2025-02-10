package com.wenl.backend.controller;

import com.wenl.backend.model.User;
import com.wenl.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Listar todos los usuarios (solo para administradores)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Cambiar rol de un usuario (solo para administradores)
    @PostMapping("/change-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> changeUserRole(@RequestParam Long userId, @RequestParam String role) {
        adminService.changeUserRole(userId, role);
        return ResponseEntity.ok("Rol del usuario actualizado exitosamente");
    }
}