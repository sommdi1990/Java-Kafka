package com.kafka.shared.repository;

import com.kafka.shared.dto.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByServiceNameAndCreatedAtBetween(
            String serviceName, LocalDateTime startDate, LocalDateTime endDate);

    List<SystemLog> findByLogLevelAndCreatedAtBetween(
            String logLevel, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM SystemLog s WHERE s.executionTimeMs > :threshold AND s.createdAt BETWEEN :startDate AND :endDate")
    List<SystemLog> findSlowExecutions(@Param("threshold") Long threshold,
                                       @Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM SystemLog s WHERE s.serviceName = :serviceName AND s.createdAt BETWEEN :startDate AND :endDate")
    Long countByServiceNameAndDateRange(@Param("serviceName") String serviceName,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}
