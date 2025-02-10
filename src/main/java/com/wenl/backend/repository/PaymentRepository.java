package com.wenl.backend.repository;

import com.wenl.backend.model.Payment;
import com.wenl.backend.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySubscription(Subscription subscription); // Obtener pagos relacionados con una suscripción

    List<Payment> findByPaymentMethod(String paymentMethod); // Filtrar pagos por método de pago
}