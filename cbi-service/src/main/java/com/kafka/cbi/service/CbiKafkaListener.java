package com.kafka.cbi.service;

import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CbiKafkaListener {

    private final ExternalApiService externalApiService;

    /**
     * Handle generic service calls coming from workflow-engine.
     * The payload is currently treated as a raw String. For a real implementation,
     * you should switch producer/consumer to JSON and bind to a DTO.
     */
    @KafkaListener(topics = "service-calls", groupId = "cbi-service-group")
    @LogExecution
    public void handleServiceCall(@Payload String message) {
        log.info("Received service-call message from Kafka: {}", message);
        // At this stage we only log. Further enhancement can parse the message
        // and delegate to ExternalApiService based on service/endpoint.
    }

    @KafkaListener(topics = "graphql-calls", groupId = "cbi-service-group")
    @LogExecution
    public void handleGraphQlCall(@Payload String message) {
        log.info("Received graphql-call message from Kafka: {}", message);
    }
}


