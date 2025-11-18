// package com.beyond.qiin.infra.kafka.reservation;
//
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// @RequestMapping("/api/v1/reservations")
// @RestController
// @RequiredArgsConstructor
// public class ReservationController {
//  private final ReservationService reservationService;
//
//  // 예약 신청
//  @PostMapping
//  public ResponseEntity<ReservationResponseDto> createReservation(
//      @Valid @RequestBody ReservationRequestDto reservationRequestDto) {
//    ReservationResponseDto reservationResponseDto =
//        reservationService.create(reservationRequestDto);
//    return ResponseEntity.status(201).body(reservationResponseDto);
//  }
// }
