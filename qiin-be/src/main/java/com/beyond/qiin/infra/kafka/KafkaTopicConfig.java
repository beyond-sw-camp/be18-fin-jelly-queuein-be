package com.beyond.qiin.infra.kafka;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
// 토픽 생성 자동화

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    // reservation: reservation-topic-dev과 같이 해당 value 값을 하드코딩하지 않는 용도로 사용
    private final KafkaTopicProperties kafkaTopicProperties;

    @Bean
    public List<NewTopic> createTopics() {
        return kafkaTopicProperties.getTopics().values().stream()
                .map(topicName -> TopicBuilder.name(topicName)
                        .partitions(3)
                        .replicas(1)
                        .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7 * 24 * 60 * 60 * 1000L)) // 7일
                        .build())
                .toList();
    }
}
