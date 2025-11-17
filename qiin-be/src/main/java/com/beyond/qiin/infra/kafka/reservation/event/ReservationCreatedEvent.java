package com.beyond.qiin.infra.kafka.reservation.event;

import com.beyond.qiin.infra.kafka.reservation.Reservation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class ReservationCreatedEvent {
    private final Long reservationId;

    public static ReservationCreatedEvent from(Reservation reservation) {
        return new ReservationCreatedEvent(reservation.getId());
    }
}
