package com.beyond.qiin.infra.event.reservation;

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
