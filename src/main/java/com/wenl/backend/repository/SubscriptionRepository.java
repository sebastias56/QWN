package com.wenl.backend.repository;

import com.wenl.backend.model.Subscription;
import com.wenl.backend.model.User;
import com.wenl.backend.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser(User user); // Buscar suscripción por usuario

    List<Subscription> findByStatus(SubscriptionStatus status); // Buscar suscripciones por estado

    List<Subscription> findByEndDateBefore(LocalDate date); // Buscar suscripciones que hayan expirado antes de una fecha

    List<Subscription> findByEndDateBetween(LocalDate startDate, LocalDate endDate); // Buscar suscripciones próximas a expirar
}