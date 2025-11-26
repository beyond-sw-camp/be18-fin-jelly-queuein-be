package com.beyond.qiin.infra.kafka.reservation.consumer;

import com.beyond.qiin.infra.event.reservation.ReservationCreatedPayload;
import com.beyond.qiin.infra.event.reservation.ReservationUpdatedPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
// 메시지 수신용 -> service 계층 따로 추가할 필요 없으므로 생략
// application 계층(notification과)의 event handler 역할(비동기) 이므로
// event listener 대신 consumer을 활용

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationConsumer {

    //    private final NotificationService notificationService;
    private final ObjectMapper objectMapper; // kafka(string) -> notification service(자바 객체)로 역직렬화용

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-created')}", groupId = "reservation-group")
    public void onCreated(String message) {
        try {
            ReservationCreatedPayload payload = objectMapper.readValue(message, ReservationCreatedPayload.class);
            log.info("received reservation-created payload: {}", payload);
            //            notificationService.notifyCreated(payload);

        } catch (Exception e) {
            log.error("Failed to handle reservation-created event", e);
        }
    }

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-updated')}", groupId = "reservation-group")
    public void onUpdated(String message) {
        try {
            ReservationUpdatedPayload payload = objectMapper.readValue(message, ReservationUpdatedPayload.class);
            log.info("received reservation-created payload: {}", payload);

            //            notificationService.notifyUpdated(payload);

        } catch (Exception e) {
            log.error("Failed to handle reservation-updated event", e);
        }
    }
}
