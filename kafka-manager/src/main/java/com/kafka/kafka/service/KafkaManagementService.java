package com.kafka.kafka.service;

import com.kafka.shared.annotation.LogExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaManagementService {

    private final AdminClient adminClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @LogExecution
    public Map<String, Object> getClusterInfo() {
        try {
            Map<String, Object> clusterInfo = new HashMap<>();

            // Get cluster ID
            String clusterId = adminClient.describeCluster().clusterId().get();
            clusterInfo.put("clusterId", clusterId);

            // Get broker information
            var brokers = adminClient.describeCluster().nodes().get();
            clusterInfo.put("brokers", brokers.size());

            // Get topics
            var topics = adminClient.listTopics().names().get();
            clusterInfo.put("topics", topics.size());

            // Get consumer groups
            var consumerGroups = adminClient.listConsumerGroups().all().get();
            clusterInfo.put("consumerGroups", consumerGroups.size());

            return clusterInfo;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get cluster info", e);
            return Map.of("error", e.getMessage());
        }
    }

    @LogExecution
    public Map<String, Object> getTopicInfo(String topicName) {
        try {
            TopicDescription topicDescription = adminClient.describeTopics(Collections.singletonList(topicName))
                    .allTopicNames().get().get(topicName);

            Map<String, Object> topicInfo = new HashMap<>();
            topicInfo.put("name", topicDescription.name());
            topicInfo.put("partitions", topicDescription.partitions().size());
            topicInfo.put("isInternal", topicDescription.isInternal());

            return topicInfo;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get topic info for: {}", topicName, e);
            return Map.of("error", e.getMessage());
        }
    }

    @LogExecution
    public Map<String, Object> getConsumerGroupInfo(String groupId) {
        try {
            ConsumerGroupDescription groupDescription = adminClient.describeConsumerGroups(Collections.singletonList(groupId))
                    .all().get().get(groupId);

            Map<String, Object> groupInfo = new HashMap<>();
            groupInfo.put("groupId", groupDescription.groupId());
            groupInfo.put("state", groupDescription.state().toString());
            groupInfo.put("members", groupDescription.members().size());

            return groupInfo;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get consumer group info for: {}", groupId, e);
            return Map.of("error", e.getMessage());
        }
    }

    @LogExecution
    public void createTopic(String topicName, int partitions, short replicationFactor) {
        try {
            adminClient.createTopics(Collections.singletonList(
                    new org.apache.kafka.clients.admin.NewTopic(topicName, partitions, replicationFactor)
            )).all().get();

            log.info("Created topic: {} with {} partitions and replication factor {}",
                    topicName, partitions, replicationFactor);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to create topic: {}", topicName, e);
            throw new RuntimeException("Failed to create topic", e);
        }
    }

    @LogExecution
    public void deleteTopic(String topicName) {
        try {
            adminClient.deleteTopics(Collections.singletonList(topicName)).all().get();
            log.info("Deleted topic: {}", topicName);

        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to delete topic: {}", topicName, e);
            throw new RuntimeException("Failed to delete topic", e);
        }
    }

    @LogExecution
    public void sendMessage(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
        log.info("Sent message to topic: {} with key: {}", topic, key);
    }

    @LogExecution
    public Map<String, Object> getTopicMetrics(String topicName) {
        try {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "metrics-consumer");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

            try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
                TopicPartition partition = new TopicPartition(topicName, 0);
                consumer.assign(Collections.singletonList(partition));

                consumer.seekToEnd(Collections.singletonList(partition));
                long endOffset = consumer.position(partition);

                consumer.seekToBeginning(Collections.singletonList(partition));
                long beginningOffset = consumer.position(partition);

                Map<String, Object> metrics = new HashMap<>();
                metrics.put("topic", topicName);
                metrics.put("totalMessages", endOffset - beginningOffset);
                metrics.put("beginningOffset", beginningOffset);
                metrics.put("endOffset", endOffset);

                return metrics;
            }

        } catch (Exception e) {
            log.error("Failed to get topic metrics for: {}", topicName, e);
            return Map.of("error", e.getMessage());
        }
    }
}
