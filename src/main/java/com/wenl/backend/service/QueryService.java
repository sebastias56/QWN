package com.wenl.backend.service;

import com.wenl.backend.enums.SubscriptionStatus;
import com.wenl.backend.model.QueryLog;
import com.wenl.backend.model.Subscription;
import com.wenl.backend.model.User;
import com.wenl.backend.repository.QueryLogRepository;
import com.wenl.backend.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private QueryLogRepository queryLogRepository;

    // Enviar consulta a QwenLM
    public String sendQueryToQwenLM(String query, User user) {
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Suscripción no encontrada"));

        if (!subscription.getStatus().equals(SubscriptionStatus.ACTIVE)) {
            throw new RuntimeException("Tu suscripción ha expirado o fue cancelada");
        }

        int maxQueries = getMaxQueriesForPlan(subscription.getPlanType());
        long queryCount = queryLogRepository.countByUser_Id(user.getId());

        if (queryCount >= maxQueries) {
            throw new RuntimeException("Has excedido el límite de consultas mensuales");
        }

        // Simulación de respuesta de QwenLM
        String response = "Respuesta simulada para: " + query;

        // Guardar el historial de consultas
        QueryLog log = new QueryLog();
        log.setQuery(query);
        log.setResponse(response);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now());
        queryLogRepository.save(log);

        return response;
    }

    // Obtener el historial de consultas de un usuario
    public List<QueryLog> getQueryHistory(Long userId) {
        return queryLogRepository.findByUser_Id(userId);
    }

    // Obtener el número máximo de consultas según el plan
    private int getMaxQueriesForPlan(String planType) {
        switch (planType) {
            case "BASIC":
                return 100;
            case "PREMIUM":
                return 500;
            case "ENTERPRISE":
                return Integer.MAX_VALUE;
            default:
                throw new RuntimeException("Plan no válido");
        }
    }
}