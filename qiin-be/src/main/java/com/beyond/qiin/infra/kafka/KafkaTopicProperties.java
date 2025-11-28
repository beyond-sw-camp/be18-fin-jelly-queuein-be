package com.beyond.qiin.infra.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// yml별 topic의 key에 따른 value들 반환 - kafka topic config에 쓰임
@Component
@ConfigurationProperties(prefix = "spring.kafka.topic")
@Getter
public class KafkaTopicProperties {

    private final Map<String, String> topics = new HashMap<>();

    public String get(String key) {
        return topics.get(key); // key에 해당하는 value 반환
    }

    public Set<String> keys() {
        return topics.keySet();
    }
}
