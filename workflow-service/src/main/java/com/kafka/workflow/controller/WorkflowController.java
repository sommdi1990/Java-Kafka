package com.kafka.workflow.controller;

import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.ApiResponse;
import com.kafka.shared.dto.WorkflowDefinition;
import com.kafka.workflow.dto.WorkflowInstance;
import com.kafka.workflow.service.WorkflowEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workflow")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowEngine workflowEngine;

    @PostMapping("/execute/{definitionId}")
    @LogExecution
    public ApiResponse<WorkflowInstance> executeWorkflow(
            @PathVariable Long definitionId,
            @RequestBody Map<String, Object> contextData) {

        // In a real implementation, you would fetch the definition from database
        WorkflowDefinition definition = createSampleDefinition(definitionId);

        WorkflowInstance instance = workflowEngine.executeWorkflow(definition, contextData);
        return ApiResponse.success("Workflow executed successfully", instance);
    }

    @PostMapping("/execute-by-name/{workflowName}")
    @LogExecution
    public ApiResponse<WorkflowInstance> executeWorkflowByName(
            @PathVariable String workflowName,
            @RequestBody Map<String, Object> contextData) {

        WorkflowDefinition definition = createSampleDefinitionByName(workflowName);
        WorkflowInstance instance = workflowEngine.executeWorkflow(definition, contextData);
        return ApiResponse.success("Workflow executed successfully", instance);
    }

    @GetMapping("/definitions")
    @LogExecution
    public ApiResponse<Object> getWorkflowDefinitions() {
        // In a real implementation, you would fetch from database
        return ApiResponse.success("Workflow definitions retrieved",
                new Object() {
                    public final String message = "Sample workflow definitions";
                    public final int count = 3;
                });
    }

    @GetMapping("/instances")
    @LogExecution
    public ApiResponse<Object> getWorkflowInstances() {
        // In a real implementation, you would fetch from database
        return ApiResponse.success("Workflow instances retrieved",
                new Object() {
                    public final String message = "Sample workflow instances";
                    public final int count = 5;
                });
    }

    private WorkflowDefinition createSampleDefinition(Long definitionId) {
        String sampleWorkflow = """
                {
                    "name": "Sample Workflow",
                    "version": "1.0",
                    "steps": [
                        {
                            "name": "step1",
                            "type": "service_call",
                            "service": "cbi-service",
                            "endpoint": "/api/cbi/rest/1"
                        },
                        {
                            "name": "step2",
                            "type": "schedule_task",
                            "task": "data-processing",
                            "cron": "0 0 12 * * ?"
                        },
                        {
                            "name": "step3",
                            "type": "notification",
                            "notificationType": "email",
                            "message": "Workflow completed successfully"
                        }
                    ]
                }
                """;

        return WorkflowDefinition.builder()
                .id(definitionId)
                .name("Sample Workflow " + definitionId)
                .version("1.0")
                .definitionJson(sampleWorkflow)
                .status(WorkflowDefinition.WorkflowStatus.ACTIVE)
                .createdBy("system")
                .build();
    }

    private WorkflowDefinition createSampleDefinitionByName(String workflowName) {
        return createSampleDefinition(1L).toBuilder()
                .name(workflowName)
                .build();
    }
}
