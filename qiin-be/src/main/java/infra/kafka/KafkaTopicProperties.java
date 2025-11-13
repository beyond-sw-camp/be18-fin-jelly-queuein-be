package infra.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
// yml별 topic의 key에 따른 value값을 하드코딩하지 않기 위한 용동

@Configuration
@ConfigurationProperties(prefix = "spring.kafka.topic")
@Getter
@Setter
public class KafkaTopicProperties {
    private String reservation;
    private String notification;
}
