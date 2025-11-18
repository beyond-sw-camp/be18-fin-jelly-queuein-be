//package com.beyond.qiin.infra.kafka.reservation;
//
//import com.beyond.qiin.infra.kafka.reservation.event.ReservationCreatedEvent;
//import com.beyond.qiin.infra.kafka.reservation.event.ReservationUpdatedEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//// mariadb에서 redis cache로
//public class ReservationConsumerService {
//    private final ReservationRepository reservationRepository;
//    private final ReservationRedisService redisService;
//
//    public void handleCreated(ReservationCreatedEvent event) {
//        Reservation reservation = reservationRepository
//                .findById(event.getReservationId())
//                .orElseThrow(() -> new IllegalStateException("Reservation not found"));
//
//        redisService.save(reservation);
//        log.info("Redis updated after reservation created: {}", event.getReservationId());
//    }
//
//    public void handleUpdated(ReservationUpdatedEvent event) {
//        Reservation reservation = reservationRepository
//                .findById(event.getReservationId())
//                .orElseThrow(() -> new IllegalStateException("Reservation not found"));
//
//        redisService.save(reservation);
//        log.info("Redis updated after reservation updated: {}", event.getReservationId());
//    }
//}
