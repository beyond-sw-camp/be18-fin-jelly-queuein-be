//package com.beyond.qiin.infra.kafka.reservation;
//
//import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class ReservationService {
//
//    private final EventPublisherService eventPublisher;
//    private final ReservationRepository reservationRepository;
//    public ReservationResponseDto create(ReservationRequestDto dto) {
//        Reservation reservation = Reservation.builder()
//                .res(dto.getRes())
//                .build();
//        reservationRepository.save(reservation);
//
//        eventPublisher.publish(ReservationCreatedEvent.from(reservation));
//
//        return ReservationResponseDto.fromEntity(reservation);
//    }
//
//    //  public Reservation updateReservation(Long id, ReservationRequestDto dto) {
//    //
//    //
//    //
//    //    // 이벤트 발행
//    //    eventPublisher.publish(new ReservationUpdatedEvent(reservation));
//    //
//    //    return reservation;
//    //  }
//
//}
