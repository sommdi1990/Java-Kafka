package com.kafka.schedule.service;

import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleKafkaListener {

    private final BatchProcessingService batchProcessingService;

    /**
     * Listens to workflow engine scheduling requests and triggers batch processing logic.
     * The payload is currently treated as a raw String for simplicity; it can be evolved
     * to a structured DTO (JSON) if needed.
     */
    @KafkaListener(topics = "schedule-tasks", groupId = "schedule-service-group")
    @LogExecution
    public void handleScheduleTask(@Payload String message) {
        log.info("Received schedule task message from Kafka: {}", message);

        // For now we trigger a generic data processing batch.
        // In a real implementation you would parse message for task name / cron / parameters.
        String batchName = "workflow-schedule-task";
        int batchSize = 100;

        batchProcessingService.processDataBatch(batchName, batchSize);
    }
}


