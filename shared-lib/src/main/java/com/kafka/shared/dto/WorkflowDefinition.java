package com.kafka.shared.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_definitions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "definition_json", columnDefinition = "TEXT")
    private String definitionJson;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum WorkflowStatus {
        DRAFT, ACTIVE, INACTIVE, ARCHIVED
    }
}
