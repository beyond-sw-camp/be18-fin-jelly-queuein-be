package com.beyond.qiin;

import com.beyond.qiin.common.config.KafkaTopicProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableConfigurationProperties
public class QiinApplication {

    public static void main(String[] args) {
        SpringApplication.run(QiinApplication.class, args);
    }
}
