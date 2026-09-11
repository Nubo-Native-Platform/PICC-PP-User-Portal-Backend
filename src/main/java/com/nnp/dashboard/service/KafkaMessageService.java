package com.nnp.dashboard.service;

import com.nnp.dashboard.utils.LogUtils;
import org.apache.kafka.clients.consumer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for publishing and consuming messages via Apache Kafka.
 * Supports sending keyed/unkeyed payload events and querying historic records by epoch timestamp range.
 */
@Service
public class KafkaMessageService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    /**
     * Sends a message payload to a specific Kafka topic using a message key.
     *
     * @param topic Kafka topic name
     * @param key message key string
     * @param message payload object
     */
    public void sendMessage(String topic,String key ,Object message) {
        kafkaTemplate.send(topic, key ,message);
    }

    /**
     * Sends a message payload to a specific Kafka topic without a message key.
     *
     * @param topic Kafka topic name
     * @param message payload object
     */
    public void sendMessage(String topic, Object message){
        kafkaTemplate.send(topic, message);
    }
    /**
     * Fetch messages for a topic within a given datetime range (UTC epoch millis).
     * @param topic Kafka topic
     * @param startTimestamp start time (epoch millis)
     * @param endTimestamp end time (epoch millis)
     * @return List of ConsumerRecord<String, Object>
     */
    public List<ConsumerRecord<String, Object>> getMessagesForTopicInRange(
            String topic, long startTimestamp, long endTimestamp) {

        List<ConsumerRecord<String, Object>> result = new ArrayList<>();
        Properties props = new Properties();
        props.putAll(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, Object> consumer = new KafkaConsumer<>(props)) {
            // Get partitions
            List<TopicPartition> partitions = consumer.partitionsFor(topic)
                    .stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .toList();

            consumer.assign(partitions);

            // Find start offsets per partition
            Map<TopicPartition, Long> timestampsToSearch = partitions.stream()
                    .collect(Collectors.toMap(p -> p, p -> startTimestamp));

            Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes = consumer.offsetsForTimes(timestampsToSearch);

            // Seek to correct start offset
            for (TopicPartition partition : partitions) {
                OffsetAndTimestamp offsetAndTimestamp = offsetsForTimes.get(partition);
                if (offsetAndTimestamp != null) {
                    consumer.seek(partition, offsetAndTimestamp.offset());
                } else {
                    // No offset found for timestamp range — skip this partition
                    consumer.seekToEnd(Collections.singleton(partition));
                }
            }

            // Pre-compute end offsets to stop earlier
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(partitions);
            boolean done = false;
            long startTime = System.currentTimeMillis();

            while (!done) {
                ConsumerRecords<String, Object> records = consumer.poll(Duration.ofMillis(500));
                if (records.isEmpty()) {
                    // Stop if no records after some timeout
                    if (System.currentTimeMillis() - startTime > 5000) break;
                    continue;
                }

                for (ConsumerRecord<String, Object> record : records) {
                    if (record.timestamp() > endTimestamp) {
                        done = true;
                        break;
                    }

                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    if (record.offset() >= endOffsets.get(tp)) {
                        continue;
                    }

                    if (record.timestamp() >= startTimestamp && record.timestamp() <= endTimestamp) {
                        result.add(record);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("Error fetching messages for topic {}: {}", LogUtils.sanitizeForLog(topic),LogUtils.sanitizeForLog( e.getMessage()
            ));
        }

        logger.info("Fetched {} messages from topic {} between {} and {}",
               LogUtils.sanitizeForLog( result.size()), LogUtils.sanitizeForLog(topic), LogUtils.sanitizeForLog(startTimestamp), LogUtils.sanitizeForLog(endTimestamp));

        return result;
    }

}
