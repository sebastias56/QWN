package com.wenl.backend.repository;

import com.wenl.backend.model.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {

    List<QueryLog> findByUser_Id(Long userId); // Buscar consultas por ID de usuario

    long countByUser_Id(Long userId); // Contar consultas por ID de usuario

    List<QueryLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end); // Filtrar consultas por rango de fechas
}