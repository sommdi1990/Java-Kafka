package com.kafka.workflow.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_instances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "workflow_definition_id", nullable = false)
    private Long workflowDefinitionId;

    @Column(name = "instance_name", nullable = false)
    private String instanceName;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InstanceStatus status;

    @Column(name = "current_step")
    private String currentStep;

    @Column(name = "context_data", columnDefinition = "TEXT")
    private String contextData;

    @Column(name = "started_by")
    private String startedBy;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "execution_time_ms")
    private Long executionTimeMs;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        status = InstanceStatus.RUNNING;
    }

    public enum InstanceStatus {
        RUNNING, COMPLETED, FAILED, PAUSED, CANCELLED
    }
}
