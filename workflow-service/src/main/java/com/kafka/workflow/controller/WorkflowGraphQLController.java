package com.kafka.workflow.controller;

import com.kafka.shared.dto.WorkflowDefinition;
import com.kafka.workflow.dto.WorkflowInstance;
import com.kafka.workflow.repository.WorkflowDefinitionRepository;
import com.kafka.workflow.repository.WorkflowInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * GraphQL endpoint for advanced querying of workflow definitions and instances.
 * <p>
 * Schema is defined in {@code src/main/resources/graphql/schema.graphqls}.
 * <p>
 * Examples:
 * - query { workflowDefinitions(status: "ACTIVE", nameContains: "Sample") { id name status } }
 * - query { workflowInstances(status: "FAILED", definitionId: 1) { id instanceName status errorMessage } }
 */
@Controller
@RequiredArgsConstructor
public class WorkflowGraphQLController {

    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final WorkflowInstanceRepository workflowInstanceRepository;

    @QueryMapping
    public List<WorkflowDefinition> workflowDefinitions(
            @Argument(name = "status") String status,
            @Argument(name = "nameContains") String nameContains) {

        List<WorkflowDefinition> all = workflowDefinitionRepository.findAll();

        return all.stream()
                .filter(def -> {
                    if (status == null || status.isBlank()) {
                        return true;
                    }
                    return def.getStatus() != null &&
                            def.getStatus().name().equalsIgnoreCase(status.trim());
                })
                .filter(def -> {
                    if (nameContains == null || nameContains.isBlank()) {
                        return true;
                    }
                    return def.getName() != null &&
                            def.getName().toLowerCase(Locale.ROOT)
                                    .contains(nameContains.toLowerCase(Locale.ROOT));
                })
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<WorkflowInstance> workflowInstances(
            @Argument(name = "status") String status,
            @Argument(name = "definitionId") Long definitionId,
            @Argument(name = "from") String from,
            @Argument(name = "to") String to) {

        List<WorkflowInstance> all = workflowInstanceRepository.findAll();

        LocalDateTime fromTime = parseDateTime(from);
        LocalDateTime toTime = parseDateTime(to);

        return all.stream()
                .filter(instance -> {
                    if (status == null || status.isBlank()) {
                        return true;
                    }
                    return instance.getStatus() != null &&
                            instance.getStatus().name().equalsIgnoreCase(status.trim());
                })
                .filter(instance -> {
                    if (definitionId == null) {
                        return true;
                    }
                    return instance.getWorkflowDefinitionId() != null &&
                            instance.getWorkflowDefinitionId().equals(definitionId);
                })
                .filter(instance -> {
                    if (fromTime == null && toTime == null) {
                        return true;
                    }
                    LocalDateTime startedAt = instance.getStartedAt();
                    if (startedAt == null) {
                        return false;
                    }
                    boolean afterFrom = fromTime == null || !startedAt.isBefore(fromTime);
                    boolean beforeTo = toTime == null || !startedAt.isAfter(toTime);
                    return afterFrom && beforeTo;
                })
                .collect(Collectors.toList());
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}


