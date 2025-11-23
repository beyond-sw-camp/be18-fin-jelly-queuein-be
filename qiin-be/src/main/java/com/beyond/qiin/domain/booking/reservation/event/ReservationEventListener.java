package com.beyond.qiin.domain.booking.reservation.event;

import com.beyond.qiin.infra.kafka.reservation.consumer.ReservationConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final ReservationConsumerService consumerService;

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-created')}")
    public void onCreated(ReservationCreatedEvent event) {
        consumerService.handleCreated(event);
    }

    @KafkaListener(topics = "#{@kafkaTopicProperties.get('reservation-created')}")
    public void onUpdated(ReservationUpdatedEvent event) {
        consumerService.handleUpdated(event);
    }
}
