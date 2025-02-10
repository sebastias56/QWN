package com.wenl.backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.wenl.backend.model.Payment;
import com.wenl.backend.model.Subscription;
import com.wenl.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private PaymentRepository paymentRepository;

    public String createPaymentIntent(int amount, Subscription subscription) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) amount)
                .setCurrency("usd")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Registrar el pago
        Payment payment = new Payment();
        payment.setAmount(amount / 100.0); // Convertir centavos a dólares
        payment.setPaymentMethod("Stripe");
        payment.setTransactionId(paymentIntent.getId());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setSubscription(subscription);
        paymentRepository.save(payment);

        return paymentIntent.getClientSecret();
    }
}