package com.wenl.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class UserUsage {
    @Id
    private UUID userId;
    private LocalDate cycleStartDate;
    private int tokensUsed; // Track DeepSeek tokens consumed
    private int apiCalls;
}