//package com.beyond.qiin.infra.kafka.reservation;
//
//import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
//import com.beyond.qiin.infra.kafka.reservation.event.ReservationUpdatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EventPublisherService {
//
//    private final ReservationProducerService reservationProducer;
//
//    public void publish(Object event) {
//
//        if (event instanceof ReservationCreatedEvent e) {
//            reservationProducer.publishReservationCreated(e);
//            return;
//        }
//
//        if (event instanceof ReservationUpdatedEvent e) {
//            reservationProducer.publishReservationUpdated(e);
//            return;
//        }
//
//        throw new IllegalArgumentException("Unknown event type: " + event.getClass());
//    }
//}
