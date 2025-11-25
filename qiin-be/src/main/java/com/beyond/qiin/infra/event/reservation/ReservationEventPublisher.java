package com.beyond.qiin.infra.event.reservation;

import com.beyond.qiin.infra.kafka.reservation.producer.ReservationProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisher {

    private final ReservationProducerService reservationProducer;

    public void publish(Object event) {

        if (event instanceof ReservationCreatedEvent e) {
            reservationProducer.publishReservationCreated(e);
            return;
        }

        if (event instanceof ReservationUpdatedEvent e) {
            reservationProducer.publishReservationUpdated(e);
            return;
        }

        // TODO: custom exception 대상
        throw new IllegalArgumentException("Unknown event type: " + event.getClass());
    }
}
