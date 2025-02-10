package com.wenl.backend.service;

import com.wenl.backend.enums.SubscriptionStatus;
import com.wenl.backend.model.Subscription;
import com.wenl.backend.model.User;
import com.wenl.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public void createSubscription(User user, String planType) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlanType(planType);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1)); // Suscripción válida por 1 mes
        subscriptionRepository.save(subscription);
    }

    public Subscription getSubscriptionByUser(User user) {
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
    }

    public void cancelSubscription(User user) {
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscriptionRepository.save(subscription);
    }

    public void updateSubscription(User user, String newPlanType) {
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));
        subscription.setPlanType(newPlanType);
        subscription.setEndDate(LocalDate.now().plusMonths(1)); // Extender la suscripción por 1 mes
        subscriptionRepository.save(subscription);
    }

    public void notifyUpcomingExpirations() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(7); // Notificar 7 días antes de la expiración
        subscriptionRepository.findByEndDateBetween(today, warningDate).forEach(subscription -> {
            System.out.println("Notificando expiración a: " + subscription.getUser().getEmail());
        });
    }
}