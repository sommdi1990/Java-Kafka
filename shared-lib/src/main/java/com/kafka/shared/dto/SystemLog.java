package com.kafka.shared.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "method_name", nullable = false)
    private String methodName;

    @Column(name = "class_name", nullable = false)
    private String className;

    @Column(name = "log_level", nullable = false)
    private String logLevel;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "exception_stack", columnDefinition = "TEXT")
    private String exceptionStack;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
