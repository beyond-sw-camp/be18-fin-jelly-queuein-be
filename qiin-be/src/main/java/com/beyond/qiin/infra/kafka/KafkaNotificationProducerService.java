package com.beyond.qiin.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaNotificationProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicProperties topicProperties;

    // message
    public void sendMessage(String message) {
        kafkaTemplate.send(topicProperties.getNotification(), message);
    }

    // topic key, message
    public void sendMessageWithKey(String key, String message) {
        kafkaTemplate.send(topicProperties.getNotification(), key, message);
    }

    // 특정 partition으로
    public void sendMessageToPartition(String message, int partition) {
        kafkaTemplate.send(topicProperties.getNotification(), partition, null, message);
    }

    // 비동기 전송 결과 처리
    public void sendMessageWithCallback(String message) {
        kafkaTemplate.send(topicProperties.getNotification(), message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Success: {} ", result.getRecordMetadata());
            } else {
                log.error("Failed: {}", ex.getMessage());
            }
        });
    }
}
