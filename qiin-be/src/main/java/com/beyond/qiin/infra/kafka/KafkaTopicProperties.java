package com.beyond.qiin.infra.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
// yml별 topic의 key에 따른 value값을 하드코딩하지 않기 위한 용도

//@RequiredArgsConstructor
@Component
@ConfigurationProperties(prefix = "spring.kafka.topic")
@Getter
public class KafkaTopicProperties {

    private String reservation;
    private String notification;
}
