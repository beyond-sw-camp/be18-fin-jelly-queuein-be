package com.beyond.qiin.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

  private final KafkaTopicProperties kafkaTopicProperties;

  @Bean
  public NewTopic reservationTopic() {
      return TopicBuilder.name(kafkaTopicProperties.getReservation())
              .partitions(3) // 파티션 수 설정
              .replicas(1) // 복제 팩터 설정 (1)
              .config( // 추가 설정
                      TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7 * 24 * 60 * 60 * 1000L) // 7일
                      )
              .build();
  }

  @Bean
  public NewTopic notificationTopic() {
      return TopicBuilder.name(kafkaTopicProperties.getNotification())
              .partitions(3) // 파티션 수 설정
              .replicas(1) // 복제 팩터 설정 (1)
              .config( // 추가 설정
                      TopicConfig.RETENTION_MS_CONFIG, String.valueOf(7 * 24 * 60 * 60 * 1000L) // 7일
                      )
              .build();
  }
}
