//package com.beyond.qiin.domain.booking.reservation.event;

//
// @Getter
// public class ReservationUpdatedEvent {
//    private final Long reservationId;
//
//    @JsonCreator
//    public ReservationUpdatedEvent(@JsonProperty("reservationId") Long reservationId) {
//        this.reservationId = reservationId;
//    }
//
//    public static ReservationUpdatedEvent from(Reservation reservation) {
//        return new ReservationUpdatedEvent(reservation.getId());
//    }
// }