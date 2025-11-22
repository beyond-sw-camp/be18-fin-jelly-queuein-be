//package com.beyond.qiin.infra.kafka.reservation;
//
//import com.beyond.qiin.infra.kafka.KafkaTopicProperties;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ReservationProducerService {
//
//    private final KafkaProducerService kafkaProducerService;
//    private final KafkaTopicProperties topics;
//
//    public void publishReservationCreated(ReservationCreatedEvent e) {
//        kafkaProducerService.sendMessage(topics.get("reservation-created"), e);
//    }
//
//    public void publishReservationUpdated(ReservationUpdatedEvent e) {
//        kafkaProducerService.sendMessage(topics.get("reservation-updated"), e);
//    }
//}
