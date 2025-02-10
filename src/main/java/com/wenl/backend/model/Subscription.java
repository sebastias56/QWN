package com.wenl.backend.model;

import com.wenl.backend.enums.SubscriptionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de plan no puede ser nulo")
    private String planType; // Ejemplo: BASIC, PREMIUM, ENTERPRISE

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status; // Estados: ACTIVE, EXPIRED, CANCELED

    private Double pricePaid; // Precio pagado por la suscripción
    private String paymentMethod; // Método de pago (ej., Stripe)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.status = SubscriptionStatus.ACTIVE;
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(1); // Suscripción válida por 1 mes
    }
}

