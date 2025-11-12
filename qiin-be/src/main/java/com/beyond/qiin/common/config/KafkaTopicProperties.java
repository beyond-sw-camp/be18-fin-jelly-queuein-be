package com.beyond.qiin.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
// yml별 topic의 key에 따른 value값을 하드코딩하지 않기 위한 용동

@Configuration
@ConfigurationProperties(prefix = "kafka.topic")
@Getter
public class KafkaTopicProperties {
    private String reservation;
    private String notification;
}
