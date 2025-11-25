package com.beyond.qiin.domain.booking.controller;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.applied_reservation.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.asset_time.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.service.command.ReservationCommandService;
import com.beyond.qiin.domain.booking.service.query.ReservationQueryService;
import com.beyond.qiin.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping("/{assetId}/apply")
    public ResponseEntity<ReservationResponseDto> createReservationApply(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("assetId") Long assetId,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto =
                reservationCommandService.applyReservation(user.getUserId(), assetId, createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 선착순 예약
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PostMapping("/{assetId}/instant-confirm")
    public ResponseEntity<ReservationResponseDto> createReservationInstantConfirm(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("assetId") Long assetId,
            @Valid @RequestBody CreateReservationRequestDto createReservationRequestDto) {
        ReservationResponseDto createReservationResponseDto = reservationCommandService.instantConfirmReservation(
                user.getUserId(), assetId, createReservationRequestDto);
        return ResponseEntity.status(201).body(createReservationResponseDto);
    }

    // 예약 승인
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/{reservationId}/approve")
    public ResponseEntity<ReservationResponseDto> approveReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto = reservationCommandService.approveReservation(
                user.getUserId(), reservationId, confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 거절
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @PatchMapping("/{reservationId}/reject")
    public ResponseEntity<ReservationResponseDto> rejectReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody ConfirmReservationRequestDto confirmReservationRequestDto) {
        // 담당자 권한인 경우
        ReservationResponseDto reservationResponseDto = reservationCommandService.rejectReservation(
                user.getUserId(), reservationId, confirmReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 시작
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/check-in")
    public ResponseEntity<ReservationResponseDto> startUsingReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("reservationId") Long reservationId,
            @RequestParam Instant startAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.startUsingReservation(user.getUserId(), reservationId, startAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 자원 사용 종료
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/check-out")
    public ResponseEntity<ReservationResponseDto> endUsingReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("reservationId") Long reservationId,
            @RequestParam Instant endAt) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.endUsingReservation(user.getUserId(), reservationId, endAt);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 자원 예약 취소
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @PatchMapping("/{reservationId}/cancel")
    public ResponseEntity<ReservationResponseDto> cancelReservation(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable("reservationId") Long reservationId) {
        ReservationResponseDto reservationResponseDto =
                reservationCommandService.cancelReservation(user.getUserId(), reservationId);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 예약 정보 변경
    @PatchMapping("/{reservationId}")
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    public ResponseEntity<ReservationResponseDto> updateReservation(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("reservationId") Long reservationId,
            @Valid @RequestBody UpdateReservationRequestDto updateReservationRequestDto) {
        ReservationResponseDto reservationResponseDto = reservationCommandService.updateReservation(
                user.getUserId(), reservationId, updateReservationRequestDto);

        URI redirectUri = URI.create("/api/v1/reservations/" + reservationId);

        return ResponseEntity.status(200).location(redirectUri).body(reservationResponseDto);
    }

    // 조회

    // 예약 상세 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable Long reservationId) {
        ReservationDetailResponseDto reservationDetailResponseDto =
                reservationQueryService.getReservation(user.getUserId(), reservationId);
        return ResponseEntity.ok(reservationDetailResponseDto);
    }

    // 페이징 대상의 조회
    // 사용자의 예약한 자원에 대한 목록 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<GetUserReservationResponseDto>> getUserReservations(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute GetUserReservationSearchCondition condition,
            Pageable pageable) {

        PageResponseDto<GetUserReservationResponseDto> page =
                reservationQueryService.getReservationsByUserId(user.getUserId(), condition, pageable);

        return ResponseEntity.ok(page);
    }

    // 예약 신청 목록 조회(관리자용)
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN', 'MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<PageResponseDto<GetAppliedReservationResponseDto>> getAppliedReservations(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute GetAppliedReservationSearchCondition condition,
            Pageable pageable) {

        PageResponseDto<GetAppliedReservationResponseDto> page =
                reservationQueryService.getReservationApplies(user.getUserId(), condition, pageable);
        return ResponseEntity.ok(page);
    }

    // 예약 가능 자원 목록 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/reservable-assets")
    public ResponseEntity<PageResponseDto<ReservableAssetResponseDto>> getReservableAssets(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @ModelAttribute ReservableAssetSearchCondition condition,
            Pageable pageable) {
        PageResponseDto<ReservableAssetResponseDto> page =
                reservationQueryService.getReservableAssets(user.getUserId(), condition, pageable);
        return ResponseEntity.ok(page);
    }

    // page x 조회

    // 월별 일정 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/monthly")
    public ResponseEntity<MonthReservationListResponseDto> getMonthlyReservations(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam YearMonth yearMonth) {
        MonthReservationListResponseDto monthReservationListResponseDto =
                reservationQueryService.getMonthlyReservations(user.getUserId(), yearMonth);
        return ResponseEntity.ok(monthReservationListResponseDto);
    }

    // 주별 일정 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/weekly")
    public ResponseEntity<WeekReservationListResponseDto> getWeeklyReservations(
            @AuthenticationPrincipal CustomUserDetails user, @RequestParam LocalDate date) {
        WeekReservationListResponseDto weekReservationListResponseDto =
                reservationQueryService.getWeeklyReservations(user.getUserId(), date);
        return ResponseEntity.ok(weekReservationListResponseDto);
    }
    //
    // 예약 가능 시간대 조회
    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @GetMapping("/{assetId}/available-times")
    public ResponseEntity<AssetTimeResponseDto> getAssetTimes(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable("assetId") Long assetId,
            @RequestParam LocalDate date) {
        AssetTimeResponseDto assetTimeResponseDto =
                reservationQueryService.getAssetTimes(user.getUserId(), assetId, date);
        return ResponseEntity.ok(assetTimeResponseDto);
    }

    @PreAuthorize("hasAnyAuthority('MASTER', 'ADMIN','GENERAL', 'MANAGER')")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @AuthenticationPrincipal CustomUserDetails user, @PathVariable("reservationId") Long reservationId) {
        reservationCommandService.sofDeleteReservation(user.getUserId(), reservationId);
        return ResponseEntity.noContent().build();
    }
}
