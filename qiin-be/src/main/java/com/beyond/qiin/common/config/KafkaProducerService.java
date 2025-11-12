package com.beyond.qiin.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topicName = "reservation-topic";

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // message
    public void sendMessage(String message) {
        kafkaTemplate.send(topicName, message);
    }

    // topic key, message
    public void sendMessageWithKey(String key, String message) {
        kafkaTemplate.send(topicName, key, message);
    }

    // 특정 partition으로
    public void sendMessageToPartition(String message, int partition) {
        kafkaTemplate.send(topicName, partition, null, message);
    }

    // 비동기 전송 결과 처리
    public void sendMessageWithCallback(String message) {
        kafkaTemplate.send(topicName, message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Success: {} ", result.getRecordMetadata());
            } else {
                log.error("Failed: {}", ex.getMessage());
            }
        });
    }
}
