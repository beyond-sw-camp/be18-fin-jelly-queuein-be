package com.beyond.qiin.domain.booking.controller.command;

import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.service.command.ReservationCommandService;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reservations")
@RestController
@RequiredArgsConstructor
public class ReservationCommandController {

    private final ReservationCommandService reservationCommandService;

    // 예약 신청
    @PostMapping("/{assetId}/apply")
    public ResponseEntity<ReservationResponseDto> createReservationApply(
            @PathVariable("assetId") Long assetId,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto =
                reservationCommandService.applyReservation(assetId, createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 선착순 예약
    @PostMapping("/{assetId}/instant-confirm")
    public ResponseEntity<ReservationResponseDto> createReservationInstantConfirm(
            @PathVariable("assetId") Long assetId,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto =
                reservationCommandService.instantConfirmReservation(assetId, createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 예약 승인
    @PatchMapping("/{reservationId}/approve")
    public ResponseEntity<ReservationResponseDto> approveReservation(
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.approveReservation(reservationId, confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 거절
    @PatchMapping("/{reservationId}/reject")
    public ResponseEntity<ReservationResponseDto> rejectReservation(
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.rejectReservation(reservationId, confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 시작
    @PatchMapping("/{reservationId}/check-in")
    public ResponseEntity<ReservationResponseDto> startUsingReservation(
            @PathVariable("reservationId") Long reservationId,
            @RequestParam Instant startAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.startUsingReservation(reservationId, startAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 종료
    @PatchMapping("/{reservationId}/check-out")
    public ResponseEntity<ReservationResponseDto> endUsingReservation(
            @PathVariable("reservationId") Long reservationId,
            @RequestParam Instant endAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.endUsingReservation(reservationId, endAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 정보 변경
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody UpdateReservationRequestDto updateReservationRequestDto) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.updateReservation(reservationId, updateReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 자원 예약 취소
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(@PathVariable("reservationId") Long reservationId) {
        ReservationResponseDto reservationResponseDto = reservationCommandService.cancelReservation(reservationId);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }
}
