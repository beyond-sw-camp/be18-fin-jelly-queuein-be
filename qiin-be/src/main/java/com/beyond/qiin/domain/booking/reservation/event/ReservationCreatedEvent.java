package com.beyond.qiin.domain.booking.reservation.event;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ReservationCreatedEvent {
  private final Long reservationId;

  @JsonCreator
  public ReservationCreatedEvent(@JsonProperty("reservationId") Long reservationId) {
      this.reservationId = reservationId;
  }

  public static ReservationCreatedEvent from(Reservation reservation) {
      return new ReservationCreatedEvent(reservation.getId());
  }
}
