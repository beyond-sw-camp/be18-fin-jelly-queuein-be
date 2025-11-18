package com.beyond.qiin.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
// 공용 producer service

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    //message
    public void sendMessage(String topic, Object message) {
        if (topic == null) {
            log.error("Kafka ERROR: topic is null. message={}", message);
        }
        kafkaTemplate.send(topic, message);
    }

    // topic key, message
    public void sendMessageWithKey(String topic, String key, Object message) {
        kafkaTemplate.send(topic, key, message);
    }

    // 특정 partition으로
    public void sendMessageToPartition(String topic, Object message, int partition) {
        kafkaTemplate.send(topic, partition, null, message);
    }

    // 비동기 전송 결과 처리
    public void sendMessageWithCallback(String topic, Object message) {
        kafkaTemplate.send(topic, message).whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Success: {} ", result.getRecordMetadata());
            } else {
                log.error("Failed: {}", ex.getMessage());
            }
        });
    }
}
