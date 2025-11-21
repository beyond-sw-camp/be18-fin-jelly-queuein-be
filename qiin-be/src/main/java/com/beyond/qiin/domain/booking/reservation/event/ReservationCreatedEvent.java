//package com.beyond.qiin.domain.booking.reservation.event;

// @Getter
// public class ReservationCreatedEvent {
//    private final Long reservationId;
//
//    @JsonCreator
//    public ReservationCreatedEvent(@JsonProperty("reservationId") Long reservationId) {
//        this.reservationId = reservationId;
//    }
//
//    public static ReservationCreatedEvent from(Reservation reservation) {
//        return new ReservationCreatedEvent(reservation.getId());
//    }
// }