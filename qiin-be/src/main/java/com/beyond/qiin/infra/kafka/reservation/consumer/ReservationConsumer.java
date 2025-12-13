package com.beyond.qiin.infra.kafka.reservation.consumer;

import com.beyond.qiin.domain.alarm.service.NotificationCommandService;
import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
// 메시지 수신용 -> service 계층 따로 추가할 필요 없으므로 생략
// application 계층(notification과)의 event handler 역할(비동기) 이므로
// event listener 대신 consumer을 활용

@Profile("!test")
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationConsumer {

    private final NotificationCommandService notificationService;
    private final ObjectMapper objectMapper; // kafka(string) -> notification service(자바 객체)로 역직렬화용

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-event')}", groupId = "reservation-group")
    public void onEvent(String message) {
        try {
            ReservationEventPayload payload = objectMapper.readValue(message, ReservationEventPayload.class);
            log.info("received reservation event payload: {}", payload);
            notificationService.notifyEvent(payload);

        } catch (Exception e) {
            log.error("Failed to handle reservation event", e);
        }
    }
}
