package com.beyond.qiin.infra.kafka.reservation;

import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
import com.beyond.qiin.infra.kafka.reservation.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {
    private final KafkaReservationConsumerService consumerService;

    @KafkaListener(topics = "${spring.kafka.topic.reservationCreated}")
    public void onCreated(ReservationCreatedEvent event) {
        consumerService.handleCreated(event);
    }

    @KafkaListener(topics = "${spring.kafka.topic.reservationUpdated}")
    public void onUpdated(ReservationUpdatedEvent event) {
        consumerService.handleUpdated(event);
    }
}
