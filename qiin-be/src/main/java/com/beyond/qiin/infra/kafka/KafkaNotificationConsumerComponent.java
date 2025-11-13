package com.beyond.qiin.infra.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
// 메시지 수신용

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNotificationConsumerComponent {
    private final KafkaNotificationConsumerService kafkaNotificationConsumerService;

    @KafkaListener(topics = "#{@kafkaTopicProperties.getNotification()}", groupId = "notification-group")
    public void listen(String message) {
        try {
            log.info("메시지 수신: {}", message);
            // 비즈니스 로직 처리
            String processedMessage = kafkaNotificationConsumerService.processMessage(message);
            log.info("메시지 처리 완료: {}", processedMessage);

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
