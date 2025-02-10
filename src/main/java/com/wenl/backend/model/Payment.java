package com.wenl.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El monto del pago no puede ser nulo")
    private Double amount;

    @NotNull(message = "El método de pago no puede ser nulo")
    private String paymentMethod; // Ejemplo: Stripe

    private String transactionId; // ID de transacción de Stripe
    private LocalDateTime paymentDate;

    @ManyToOne
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
}