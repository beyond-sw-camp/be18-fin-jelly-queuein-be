package com.beyond.qiin.common.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.topic")
@Getter
public class KafkaTopicProperties {
    private String reservation;
    private String notification;
}
