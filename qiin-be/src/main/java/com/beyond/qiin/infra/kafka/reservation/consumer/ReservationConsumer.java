package com.beyond.qiin.infra.kafka.reservation.consumer;

import com.beyond.qiin.domain.alarm.service.NotificationCommandService;
import com.beyond.qiin.infra.event.reservation.ReservationEventPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import org.springframework.retry.annotation.Backoff;

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

    @RetryableTopic(
        attempts = "5", //첫 시도 + 재시도 4번 - 시스템 부하 안되되 일시적 장애 보완용
        backoff = @Backoff(delay = 1000, multiplier = 2), //1, 2, 4, ... 와 같이 늘어남 - 장애 시 시간 걸림 예상
        dltTopicSuffix = ".dlt" //topic.dlt에 저장
    )
    @KafkaListener(
        topics = "#{@kafkaTopicProperties.get('reservation-event')}"
    ) //group-id는 yml별 구분

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
