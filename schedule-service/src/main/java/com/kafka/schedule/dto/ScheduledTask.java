package com.kafka.schedule.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_tasks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false, unique = true)
    private String taskName;

    @Column(name = "description")
    private String description;

    @Column(name = "cron_expression", nullable = false)
    private String cronExpression;

    @Column(name = "task_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskType taskType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "last_execution")
    private LocalDateTime lastExecution;

    @Column(name = "next_execution")
    private LocalDateTime nextExecution;

    @Column(name = "execution_count")
    private Long executionCount;

    @Column(name = "success_count")
    private Long successCount;

    @Column(name = "failure_count")
    private Long failureCount;

    @Column(name = "average_execution_time_ms")
    private Long averageExecutionTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        executionCount = 0L;
        successCount = 0L;
        failureCount = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TaskType {
        DATA_PROCESSING, REPORT_GENERATION, DATA_CLEANUP, NOTIFICATION, INTEGRATION
    }
}
