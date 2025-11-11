package com.beyond.qiin.booking.controller.command;

import com.beyond.qiin.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.response.CreateReservationResponseDto;
import com.beyond.qiin.booking.reservation.entity.Reservation;
import com.beyond.qiin.booking.reservation.service.command.ReservationCommandService;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationCommandController {

  private final ReservationCommandService reservationCommandService;

  //예약 신청
  @PostMapping("/{assetId}/apply")
  public ResponseEntity<CreateReservationResponseDto> createReservationApply(@PathVariable("assetId") Long assetId,
      @RequestBody CreateReservationRequestDto createReservationRequestDto) {
    CreateReservationResponseDto createReservationResponseDto = reservationCommandService.applyReservation(assetId, createReservationRequestDto);
    return ResponseEntity.status(201).body(createReservationResponseDto);
  }


  //선착순 예약
  @PostMapping("/{assetId}/instant-confirm")
  public ResponseEntity<CreateReservationResponseDto> createReservationInstantConfirm(@PathVariable("assetId") Long assetId,
      @RequestBody CreateReservationRequestDto createReservationRequestDto){
    CreateReservationResponseDto createReservationResponseDto = reservationCommandService.instantConfirmReservation(assetId, createReservationRequestDto);
    return ResponseEntity.status(201).body(createReservationResponseDto);
  }

  //예약 승인
  @PatchMapping("/{reservationId}/approve")
  public ResponseEntity<Void> approveReservation(
      @PathVariable("reservationId") Long reservationId
      //관리자명 이나 뭐
  ) {
    reservationCommandService.approveReservation(reservationId);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

  //예약 거절
  @PatchMapping("/{reservationId}/reject")
  public ResponseEntity<Void> rejectReservation(
      @PathVariable("reservationId") Long reservationId
  ) {
    //관리자명이나 그런거
    reservationCommandService.rejectReservation(reservationId);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

  //예약 자원 사용 시작
  @PatchMapping("/{reservationId}/check-in")
  public ResponseEntity<Void> startUsingReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam Instant startAt) {
    reservationCommandService.startUsingReservation(reservationId, startAt);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

  //예약 자원 사용 종료
  @PatchMapping("/{reservationId}/check-out")
  public ResponseEntity<Void> endUsingReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestParam Instant endAt) {
    reservationCommandService.endUsingReservation(reservationId, endAt);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

  //예약 정보 변경
  @PatchMapping("/{reservationId}")
  public ResponseEntity<Void> updateReservation(
      @PathVariable("reservationId") Long reservationId,
      @RequestBody UpdateReservationRequestDto updateReservationRequestDto) {
    reservationCommandService.updateReservation(reservationId, updateReservationRequestDto);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

  //자원 예약 취소
  @PatchMapping("/{reservationId}/cancel")
  public ResponseEntity<Void> cancelReservation(@PathVariable("reservationId") Long reservationId, @RequestBody CreateReservationRequestDto createReservationRequestDto) {
    reservationCommandService.cancelReservation(reservationId);

    URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

    return ResponseEntity.status(200)
        .location(redirectUri)
        .build();
  }

}
