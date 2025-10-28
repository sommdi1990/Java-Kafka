package com.kafka.workflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.WorkflowDefinition;
import com.kafka.workflow.dto.WorkflowInstance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowEngine {

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @LogExecution
    public WorkflowInstance executeWorkflow(WorkflowDefinition definition, Map<String, Object> contextData) {
        log.info("Starting workflow execution: {}", definition.getName());

        WorkflowInstance instance = WorkflowInstance.builder()
                .workflowDefinitionId(definition.getId())
                .instanceName(definition.getName() + "_" + System.currentTimeMillis())
                .contextData(convertContextToString(contextData))
                .startedBy("system")
                .build();

        try {
            JsonNode workflowJson = objectMapper.readTree(definition.getDefinitionJson());
            processWorkflowSteps(workflowJson, instance, contextData);

            instance.setStatus(WorkflowInstance.InstanceStatus.COMPLETED);
            instance.setCompletedAt(LocalDateTime.now());
            instance.setExecutionTimeMs(System.currentTimeMillis() - instance.getStartedAt().toEpochSecond(java.time.ZoneOffset.UTC) * 1000);

            log.info("Workflow execution completed: {}", instance.getInstanceName());

        } catch (Exception e) {
            instance.setStatus(WorkflowInstance.InstanceStatus.FAILED);
            instance.setErrorMessage(e.getMessage());
            instance.setCompletedAt(LocalDateTime.now());

            log.error("Workflow execution failed: {}", instance.getInstanceName(), e);
        }

        return instance;
    }

    @LogExecution
    private void processWorkflowSteps(JsonNode workflowJson, WorkflowInstance instance, Map<String, Object> contextData) {
        JsonNode steps = workflowJson.get("steps");

        if (steps != null && steps.isArray()) {
            for (JsonNode step : steps) {
                String stepName = step.get("name").asText();
                String stepType = step.get("type").asText();

                log.info("Executing step: {} of type: {}", stepName, stepType);

                instance.setCurrentStep(stepName);

                switch (stepType) {
                    case "service_call":
                        executeServiceCall(step, contextData);
                        break;
                    case "schedule_task":
                        executeScheduleTask(step, contextData);
                        break;
                    case "data_processing":
                        executeDataProcessing(step, contextData);
                        break;
                    case "notification":
                        executeNotification(step, contextData);
                        break;
                    case "condition":
                        executeCondition(step, contextData);
                        break;
                    default:
                        log.warn("Unknown step type: {}", stepType);
                }
            }
        }
    }

    @LogExecution
    private void executeServiceCall(JsonNode step, Map<String, Object> contextData) {
        String serviceName = step.get("service").asText();
        String endpoint = step.get("endpoint").asText();

        log.info("Calling service: {} at endpoint: {}", serviceName, endpoint);

        // Send message to Kafka for service call
        Map<String, Object> serviceCallMessage = Map.of(
                "service", serviceName,
                "endpoint", endpoint,
                "context", contextData,
                "timestamp", LocalDateTime.now().toString()
        );

        kafkaTemplate.send("service-calls", serviceCallMessage);
    }

    @LogExecution
    private void executeScheduleTask(JsonNode step, Map<String, Object> contextData) {
        String taskName = step.get("task").asText();
        String cronExpression = step.get("cron").asText();

        log.info("Scheduling task: {} with cron: {}", taskName, cronExpression);

        // Send message to Kafka for task scheduling
        Map<String, Object> scheduleMessage = Map.of(
                "task", taskName,
                "cron", cronExpression,
                "context", contextData,
                "timestamp", LocalDateTime.now().toString()
        );

        kafkaTemplate.send("schedule-tasks", scheduleMessage);
    }

    @LogExecution
    private void executeDataProcessing(JsonNode step, Map<String, Object> contextData) {
        String processingType = step.get("processingType").asText();
        int batchSize = step.get("batchSize").asInt();

        log.info("Executing data processing: {} with batch size: {}", processingType, batchSize);

        // Send message to Kafka for data processing
        Map<String, Object> processingMessage = Map.of(
                "processingType", processingType,
                "batchSize", batchSize,
                "context", contextData,
                "timestamp", LocalDateTime.now().toString()
        );

        kafkaTemplate.send("data-processing", processingMessage);
    }

    @LogExecution
    private void executeNotification(JsonNode step, Map<String, Object> contextData) {
        String notificationType = step.get("notificationType").asText();
        String message = step.get("message").asText();

        log.info("Sending notification: {} with message: {}", notificationType, message);

        // Send message to Kafka for notification
        Map<String, Object> notificationMessage = Map.of(
                "notificationType", notificationType,
                "message", message,
                "context", contextData,
                "timestamp", LocalDateTime.now().toString()
        );

        kafkaTemplate.send("notifications", notificationMessage);
    }

    @LogExecution
    private void executeCondition(JsonNode step, Map<String, Object> contextData) {
        String condition = step.get("condition").asText();
        String trueStep = step.get("trueStep").asText();
        String falseStep = step.get("falseStep").asText();

        log.info("Evaluating condition: {}", condition);

        // Simple condition evaluation (in real implementation, use a proper expression evaluator)
        boolean conditionResult = evaluateCondition(condition, contextData);

        if (conditionResult) {
            log.info("Condition is true, executing step: {}", trueStep);
        } else {
            log.info("Condition is false, executing step: {}", falseStep);
        }
    }

    private boolean evaluateCondition(String condition, Map<String, Object> contextData) {
        // Simple condition evaluation - in real implementation, use a proper expression evaluator
        return condition.contains("true") || condition.contains("1");
    }

    private String convertContextToString(Map<String, Object> contextData) {
        try {
            return objectMapper.writeValueAsString(contextData);
        } catch (Exception e) {
            log.error("Failed to convert context to string", e);
            return "{}";
        }
    }
}
