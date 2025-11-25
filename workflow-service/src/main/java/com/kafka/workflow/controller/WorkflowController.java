package com.kafka.workflow.controller;

import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.ApiResponse;
import com.kafka.shared.dto.WorkflowDefinition;
import com.kafka.workflow.dto.WorkflowInstance;
import com.kafka.workflow.repository.WorkflowInstanceRepository;
import com.kafka.workflow.service.WorkflowDefinitionService;
import com.kafka.workflow.service.WorkflowEngine;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.kafka.shared.dto.WorkflowDefinition.WorkflowStatus;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowEngine workflowEngine;
    private final WorkflowDefinitionService workflowDefinitionService;
    private final WorkflowInstanceRepository workflowInstanceRepository;

    @PostMapping("/execute/{definitionId}")
    @LogExecution
    public ApiResponse<WorkflowInstance> executeWorkflow(
            @PathVariable Long definitionId,
            @RequestBody Map<String, Object> contextData) {

        WorkflowDefinition definition = workflowDefinitionService.getById(definitionId);
        WorkflowInstance instance = workflowEngine.executeWorkflow(definition, contextData);
        workflowInstanceRepository.save(instance);
        return ApiResponse.success("Workflow executed successfully", instance);
    }

    @PostMapping("/execute-by-name/{workflowName}")
    @LogExecution
    public ApiResponse<WorkflowInstance> executeWorkflowByName(
            @PathVariable String workflowName,
            @RequestBody Map<String, Object> contextData) {

        WorkflowDefinition definition = workflowDefinitionService.getByName(workflowName);
        WorkflowInstance instance = workflowEngine.executeWorkflow(definition, contextData);
        workflowInstanceRepository.save(instance);
        return ApiResponse.success("Workflow executed successfully", instance);
    }

    @GetMapping("/definitions")
    @LogExecution
    public ApiResponse<List<WorkflowDefinition>> getWorkflowDefinitions() {
        List<WorkflowDefinition> definitions = workflowDefinitionService.getAllDefinitions();
        return ApiResponse.success("Workflow definitions retrieved successfully", definitions);
    }

    @GetMapping("/definitions/{id}")
    @LogExecution
    public ApiResponse<WorkflowDefinition> getWorkflowDefinition(@PathVariable Long id) {
        WorkflowDefinition definition = workflowDefinitionService.getById(id);
        return ApiResponse.success("Workflow definition retrieved successfully", definition);
    }

    @GetMapping("/instances")
    @LogExecution
    public ApiResponse<Object> getWorkflowInstances() {
        return ApiResponse.success("Workflow instances retrieved",
                workflowInstanceRepository.findAll());
    }

    @GetMapping("/instances/{id}")
    @LogExecution
    public ApiResponse<WorkflowInstance> getWorkflowInstance(@PathVariable Long id) {
        WorkflowInstance instance = workflowInstanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workflow instance not found: " + id));
        return ApiResponse.success("Workflow instance retrieved successfully", instance);
    }

    @PostMapping("/definitions")
    @LogExecution
    public ApiResponse<WorkflowDefinition> createWorkflowDefinition(@Valid @RequestBody WorkflowDefinition definition) {
        WorkflowDefinition created = workflowDefinitionService.create(definition);
        return ApiResponse.success("Workflow definition created successfully", created);
    }

    @PutMapping("/definitions/{id}")
    @LogExecution
    public ApiResponse<WorkflowDefinition> updateWorkflowDefinition(
            @PathVariable Long id,
            @Valid @RequestBody WorkflowDefinition definition) {

        WorkflowDefinition updated = workflowDefinitionService.update(id, definition);
        return ApiResponse.success("Workflow definition updated successfully", updated);
    }

    @PatchMapping("/definitions/{id}/status")
    @LogExecution
    public ApiResponse<WorkflowDefinition> updateWorkflowStatus(
            @PathVariable Long id,
            @RequestParam WorkflowStatus status) {

        WorkflowDefinition updated = workflowDefinitionService.updateStatus(id, status);
        return ApiResponse.success("Workflow status updated successfully", updated);
    }
}
