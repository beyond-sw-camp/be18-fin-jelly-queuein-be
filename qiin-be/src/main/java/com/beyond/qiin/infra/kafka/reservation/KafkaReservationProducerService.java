package com.beyond.qiin.infra.kafka.reservation;

import com.beyond.qiin.infra.kafka.KafkaProducerService;
import com.beyond.qiin.infra.kafka.KafkaTopicProperties;
import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
import com.beyond.qiin.infra.kafka.reservation.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
//reservation service에서 호출
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaReservationProducerService {

    private final KafkaProducerService kafkaProducerService;
    private final KafkaTopicProperties topics;

    public void publishReservationCreated(ReservationCreatedEvent e){
        kafkaProducerService.sendMessage(topics.get("reservationCreated"), e);
    }

    public void publishReservationUpdated(ReservationUpdatedEvent e){
        kafkaProducerService.sendMessage(topics.get("reservationUpdated"), e);
    }

}
