package com.beyond.qiin.infra.kafka;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
// application 시작 시 spring yml에서 topic value들로 토픽 생성 자동화
// config에 해당하나 프레임워크 전반이 아니라 kafka 전용의 설정이므로 kafka 폴더 안에 넣음(카프카의 토픽 설정을 위한 것)

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

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
