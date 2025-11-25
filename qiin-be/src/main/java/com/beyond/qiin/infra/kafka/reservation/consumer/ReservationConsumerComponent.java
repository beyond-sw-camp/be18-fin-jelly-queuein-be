package com.beyond.qiin.infra.kafka.reservation.consumer;

import com.beyond.qiin.domain.booking.event.ReservationCreatedEvent;
import com.beyond.qiin.domain.booking.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
// 메시지 수신용
// TODO : Reservation Event Listener과 중복됨 - 뭘로 할지 정해야함

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationConsumerComponent {

    private final ReservationConsumerService consumerService;

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-created')}", groupId = "reservation-group")
    public void onReservationCreated(ReservationCreatedEvent event) {
        log.info("Received ReservationCreatedEvent: {}", event);
        consumerService.handleCreated(event);
    }

    // 레디스 캐시용이므로 update의 내용 세분화 x
    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-updated')}", groupId = "reservation-group")
    public void onReservationUpdated(ReservationUpdatedEvent event) {
        log.info("Received ReservationUpdatedEvent: {}", event);
        consumerService.handleUpdated(event);
    }

    // ui상, 논리상 삭제 없으므로 생략
}
