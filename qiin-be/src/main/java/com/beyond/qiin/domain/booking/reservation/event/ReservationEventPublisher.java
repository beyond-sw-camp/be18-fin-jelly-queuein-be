// package com.beyond.qiin.domain.booking.reservation.event;
//
// import org.springframework.stereotype.Component;
//
// @Component
// public class ReservationEventPublisher {
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
//        //TODO: custom exception 대상
//        throw new IllegalArgumentException("Unknown event type: " + event.getClass());
//    }
// }
