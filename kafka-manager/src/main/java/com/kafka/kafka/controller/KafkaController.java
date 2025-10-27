package com.kafka.kafka.controller;

import com.kafka.kafka.service.KafkaManagementService;
import com.kafka.shared.annotation.LogExecution;
import com.kafka.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaManagementService kafkaManagementService;

    @GetMapping("/cluster")
    @LogExecution
    public ApiResponse<Map<String, Object>> getClusterInfo() {
        Map<String, Object> clusterInfo = kafkaManagementService.getClusterInfo();
        return ApiResponse.success("Cluster info retrieved successfully", clusterInfo);
    }

    @GetMapping("/topics/{topicName}")
    @LogExecution
    public ApiResponse<Map<String, Object>> getTopicInfo(@PathVariable String topicName) {
        Map<String, Object> topicInfo = kafkaManagementService.getTopicInfo(topicName);
        return ApiResponse.success("Topic info retrieved successfully", topicInfo);
    }

    @GetMapping("/topics/{topicName}/metrics")
    @LogExecution
    public ApiResponse<Map<String, Object>> getTopicMetrics(@PathVariable String topicName) {
        Map<String, Object> metrics = kafkaManagementService.getTopicMetrics(topicName);
        return ApiResponse.success("Topic metrics retrieved successfully", metrics);
    }

    @GetMapping("/consumer-groups/{groupId}")
    @LogExecution
    public ApiResponse<Map<String, Object>> getConsumerGroupInfo(@PathVariable String groupId) {
        Map<String, Object> groupInfo = kafkaManagementService.getConsumerGroupInfo(groupId);
        return ApiResponse.success("Consumer group info retrieved successfully", groupInfo);
    }

    @PostMapping("/topics")
    @LogExecution
    public ApiResponse<String> createTopic(
            @RequestParam String topicName,
            @RequestParam(defaultValue = "3") int partitions,
            @RequestParam(defaultValue = "1") short replicationFactor) {

        kafkaManagementService.createTopic(topicName, partitions, replicationFactor);
        return ApiResponse.success("Topic created successfully", topicName);
    }

    @DeleteMapping("/topics/{topicName}")
    @LogExecution
    public ApiResponse<String> deleteTopic(@PathVariable String topicName) {
        kafkaManagementService.deleteTopic(topicName);
        return ApiResponse.success("Topic deleted successfully", topicName);
    }

    @PostMapping("/messages")
    @LogExecution
    public ApiResponse<String> sendMessage(
            @RequestParam String topic,
            @RequestParam String key,
            @RequestBody Object message) {

        kafkaManagementService.sendMessage(topic, key, message);
        return ApiResponse.success("Message sent successfully", "Message sent to topic: " + topic);
    }
}
