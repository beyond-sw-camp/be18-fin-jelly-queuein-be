package com.beyond.qiin.domain.booking.event;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class    ReservationCreatedEvent {
    private final Long reservationId;

    @JsonCreator
    public ReservationCreatedEvent(@JsonProperty("reservationId") Long reservationId) {
        this.reservationId = reservationId;
    }

    //Reservation reservation 보다 id를 써주는게 결합도를 줄일 수 있음
    public static ReservationCreatedEvent from(Long reservationId) {
        return new ReservationCreatedEvent(reservationId);
    }
}
