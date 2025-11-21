package com.beyond.qiin.domain.booking.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.service.command.ReservationCommandService;
import com.beyond.qiin.domain.booking.reservation.service.query.ReservationQueryService;
import com.beyond.qiin.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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
public class ReservationController {

    private final ReservationCommandService reservationCommandService;
    private final ReservationQueryService reservationQueryService;

    // 예약 신청
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping("/{assetId}/apply")
    public ResponseEntity<ReservationResponseDto> createReservationApply(
            @PathVariable("assetId") Long assetId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto =
                reservationCommandService.applyReservation(assetId, user.getUserId(), createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 선착순 예약
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping("/{assetId}/instant-confirm")
    public ResponseEntity<ReservationResponseDto> createReservationInstantConfirm(
            @PathVariable("assetId") Long assetId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto = reservationCommandService.instantConfirmReservation(
                assetId, user.getUserId(), createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 예약 승인
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/{reservationId}/approve")
    public ResponseEntity<ReservationResponseDto> approveReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto = reservationCommandService.approveReservation(
                reservationId, user.getUserId(), confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 거절
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/{reservationId}/reject")
    public ResponseEntity<ReservationResponseDto> rejectReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto = reservationCommandService.rejectReservation(
                reservationId, user.getUserId(), confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 시작
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/check-in")
    public ResponseEntity<ReservationResponseDto> startUsingReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam Instant startAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.startUsingReservation(reservationId, user.getUserId(), startAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 종료
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/check-out")
    public ResponseEntity<ReservationResponseDto> endUsingReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam Instant endAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.endUsingReservation(reservationId, user.getUserId(), endAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 자원 예약 취소
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @PathVariable("reservationId") Long reservationId, @AuthenticationPrincipal CustomUserDetails user) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.cancelReservation(reservationId, user.getUserId());

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 정보 변경
    @PatchMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @PathVariable("reservationId") Long reservationId,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody UpdateReservationRequestDto updateReservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationCommandService.updateReservation(
                user.getUserId(), reservationId, updateReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 조회

    // 예약 상세 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(
            @PathVariable Long reservationId, @AuthenticationPrincipal CustomUserDetails user) {
        ReservationDetailResponseDto reservationDetailResponseDto =
                reservationQueryService.getReservation(reservationId, user.getUserId());
        return ResponseEntity.ok(reservationDetailResponseDto);
    }

    // 페이징 대상의 조회
    // 사용자의 예약한 자원에 대한 목록 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<GetUserReservationResponseDto>> getUserReservations(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody GetUserReservationSearchCondition condition,
            Pageable pageable) {

        PageResponseDto<GetUserReservationResponseDto> page =
                reservationQueryService.getReservationsByUserId(user.getUserId(), condition, pageable);

        return ResponseEntity.ok(page);
    }

    // 예약 가능 자원 목록 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/{assetId}")
    public ResponseEntity<PageResponseDto<ReservableAssetResponseDto>> getReservableAssets(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody ReservableAssetSearchCondition condition,
            Pageable pageable) {
        PageResponseDto<ReservableAssetResponseDto> page =
                reservationQueryService.getReservableAssets(user.getUserId(), condition, pageable);
        return ResponseEntity.ok(page);
    }

    // 예약 신청 목록 조회(관리자용)
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<PageResponseDto<GetAppliedReservationResponseDto>> getAppliedReservations(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody GetAppliedReservationSearchCondition condition,
            Pageable pageable) {

        PageResponseDto<GetAppliedReservationResponseDto> page =
                reservationQueryService.getReservationApplies(user.getUserId(), condition, pageable);
        return ResponseEntity.ok(page);
    }

    // page x 조회

    // 월별 일정 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/monthly")
    public ResponseEntity<MonthReservationListResponseDto> getMonthlyReservations(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam Instant from, @RequestParam Instant to) {
        MonthReservationListResponseDto monthReservationListResponseDto =
                reservationQueryService.getMonthlyReservations(user.getUserId(), from, to);
        return ResponseEntity.ok(monthReservationListResponseDto);
    }

    // 주별 일정 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/weekly")
    public ResponseEntity<WeekReservationListResponseDto> getWeeklyReservations(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam Instant start, @RequestParam Instant end) {
        WeekReservationListResponseDto weekReservationListResponseDto =
                reservationQueryService.getWeeklyReservations(user.getUserId(), start, end);
        return ResponseEntity.ok(weekReservationListResponseDto);
    }

    // 예약 가능 시간대 조회
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/{assetId}/times")
    public ResponseEntity<AssetTimeResponseDto> getAssetTimes(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam Long assetId, @RequestParam LocalDate date) {
        AssetTimeResponseDto assetTimeResponseDto =
                reservationQueryService.getAssetTimes(user.getUserId(), assetId, date);
        return ResponseEntity.ok(assetTimeResponseDto);
    }
}
