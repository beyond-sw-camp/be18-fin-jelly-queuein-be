package com.beyond.qiin.infra.kafka.reservation;

import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
import com.beyond.qiin.infra.kafka.reservation.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final EventPublisherService eventPublisher;

  public Reservation createReservation(ReservationRequestDto dto) {
    Reservation reservation = Reservation.builder()
        .startAt(dto.getStartAt())
        .endAt(dto.getEndAt())
        .description(dto.getDescription())
        .status(0)         // pending
        .isApproved(false) // default
        .build();

    eventPublisher.publish(ReservationCreatedEvent.from(reservation));

    return reservation;

  }

//  public Reservation updateReservation(Long id, ReservationRequestDto dto) {
//
//
//
//    // 이벤트 발행
//    eventPublisher.publish(new ReservationUpdatedEvent(reservation));
//
//    return reservation;
//  }


}
