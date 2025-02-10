package com.wenl.backend.controller;

import com.wenl.backend.model.Subscription;
import com.wenl.backend.model.User;
import com.wenl.backend.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // Crear suscripción
    @PostMapping("/create")
    public ResponseEntity<String> createSubscription(@RequestBody Map<String, String> request, Authentication authentication) {
        String planType = request.get("planType");
        User user = (User) authentication.getPrincipal();

        subscriptionService.createSubscription(user, planType);
        return ResponseEntity.ok("Suscripción creada exitosamente");
    }

    // Obtener suscripción actual del usuario
    @GetMapping("/current")
    public ResponseEntity<Subscription> getCurrentSubscription(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Subscription subscription = subscriptionService.getSubscriptionByUser(user);
        return ResponseEntity.ok(subscription);
    }

    // Cancelar suscripción
    @PostMapping("/cancel")
    public ResponseEntity<String> cancelSubscription(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        subscriptionService.cancelSubscription(user);
        return ResponseEntity.ok("Suscripción cancelada exitosamente");
    }

    // Actualizar suscripción
    @PostMapping("/update")
    public ResponseEntity<String> updateSubscription(@RequestBody Map<String, String> request, Authentication authentication) {
        String newPlanType = request.get("planType");
        User user = (User) authentication.getPrincipal();

        subscriptionService.updateSubscription(user, newPlanType);
        return ResponseEntity.ok("Suscripción actualizada exitosamente");
    }
}