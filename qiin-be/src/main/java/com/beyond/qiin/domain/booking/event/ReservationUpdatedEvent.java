package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReservationUpdatedEvent {
    private final Long reservationId;

    @JsonCreator
    public ReservationUpdatedEvent(@JsonProperty("reservationId") Long reservationId) {
        this.reservationId = reservationId;
    }

    public static ReservationUpdatedEvent from(Long reservationId) {
        return new ReservationUpdatedEvent(reservationId);
    }
}
