package com.wenl.backend.controller;

import com.wenl.backend.model.QueryLog;
import com.wenl.backend.model.User;
import com.wenl.backend.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queries")
public class QueryController {

    @Autowired
    private QueryService queryService;

    // Enviar consulta a QwenLM
    @PostMapping
    public ResponseEntity<String> handleQuery(@RequestBody Map<String, String> request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String query = request.get("query");

        if (query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().body("La consulta no puede estar vacía");
        }

        String response = queryService.sendQueryToQwenLM(query, user);
        return ResponseEntity.ok(response);
    }

    // Obtener historial de consultas del usuario
    @GetMapping("/history")
    public ResponseEntity<List<QueryLog>> getQueryHistory(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<QueryLog> history = queryService.getQueryHistory(user.getId());
        return ResponseEntity.ok(history);
    }
}