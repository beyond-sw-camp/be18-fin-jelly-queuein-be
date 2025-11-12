package com.beyond.qiin.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic userTopic() {
    return TopicBuilder.name("user-topic")
        .partitions(3)                          // 파티션 수 설정
        .replicas(1)                             // 복제 팩터 설정 (1)
        .config(                                            // 추가 설정
            TopicConfig.RETENTION_MS_CONFIG,
            String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
        )
        .build();
  }

  @Bean
  public NewTopic assetTopic() {
    return TopicBuilder.name("asset-topic")
        .partitions(3)                          // 파티션 수 설정
        .replicas(1)                             // 복제 팩터 설정 (1)
        .config(                                            // 추가 설정
            TopicConfig.RETENTION_MS_CONFIG,
            String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
        )
        .build();
  }

  @Bean
  public NewTopic reservationTopic() {
    return TopicBuilder.name("reservation-topic")
        .partitions(3)                          // 파티션 수 설정
        .replicas(1)                             // 복제 팩터 설정 (1)
        .config(                                            // 추가 설정
            TopicConfig.RETENTION_MS_CONFIG,
            String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
        )
        .build();
  }

  @Bean
  public NewTopic historyTopic() {
    return TopicBuilder.name("history-topic")
        .partitions(3)                          // 파티션 수 설정
        .replicas(1)                             // 복제 팩터 설정 (1)
        .config(                                            // 추가 설정
            TopicConfig.RETENTION_MS_CONFIG,
            String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
        )
        .build();
  }

  @Bean
  public NewTopic settlementTopic() {
    return TopicBuilder.name("settlement-topic")
        .partitions(3)                          // 파티션 수 설정
        .replicas(1)                             // 복제 팩터 설정 (1)
        .config(                                            // 추가 설정
            TopicConfig.RETENTION_MS_CONFIG,
            String.valueOf(7 * 24 * 60 * 60 * 1000L)  // 7일
        )
        .build();
  }

}
