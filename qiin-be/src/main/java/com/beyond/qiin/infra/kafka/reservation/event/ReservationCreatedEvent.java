//package com.beyond.qiin.infra.kafka.reservation.event;
//
//import com.beyond.qiin.infra.kafka.reservation.Reservation;
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//
//@Getter
//public class ReservationCreatedEvent {
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
//}
