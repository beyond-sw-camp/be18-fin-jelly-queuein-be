package com.beyond.qiin.domain.booking.controller.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.service.query.ReservationQueryService;
import com.beyond.qiin.security.CustomUserDetails;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reservations")
@RestController
@RequiredArgsConstructor
public class ReservationQueryController {

    private final ReservationQueryService reservationQueryService;

    // 예약 상세 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(
        @PathVariable Long reservationId,
        @AuthenticationPrincipal CustomUserDetails user) {
        ReservationDetailResponseDto reservationDetailResponseDto =
                reservationQueryService.getReservation(reservationId, user.getUserId());
        return ResponseEntity.ok(reservationDetailResponseDto);
    }

    // 사용자의 예약한 자원에 대한 목록 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<GetUserReservationResponseDto>> getUserReservations(
        @PathVariable Long userId,
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam LocalDate date,
        Pageable pageable) {

        Page<GetUserReservationResponseDto> page =
                reservationQueryService.getReservationsByUserId(user.getUserId(), date, pageable);

        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 예약 가능 자원 목록 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    @GetMapping("/{assetId}")
    public ResponseEntity<PageResponseDto<ReservableAssetResponseDto>> getReservableAssets(
        @RequestParam LocalDate date,
        @AuthenticationPrincipal CustomUserDetails user,
        Pageable pageable) {
        Page<GetUserReservationResponseDto> page = reservationQueryService.getReservableAssets(
            user.getUserId(), date, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 예약 신청 목록 조회(관리자용)
    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<PageResponseDto<GetAppliedReservationResponseDto>> getAppliedReservations(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam LocalDate date,
        Pageable pageable) {

        Page<GetAppliedReservationResponseDto> page = reservationQueryService.getReservationApplies(
            user.getUserId(), date, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 월별 일정 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    @GetMapping("/monthly")
    public ResponseEntity<MonthReservationListResponseDto> getMonthlyReservations(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long userId,
        @RequestParam YearMonth yearMonth) {
        MonthReservationListResponseDto monthReservationListResponseDto =
                reservationQueryService.getMonthlyReservations(
                    user.getUserId(), yearMonth);
        return ResponseEntity.ok(monthReservationListResponseDto);
    }

    // 주별 일정 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    @GetMapping("/weekly")
    public ResponseEntity<WeekReservationListResponseDto> getWeeklyReservations(
        @AuthenticationPrincipal CustomUserDetails user,
        @PathVariable Long userId,
        @RequestParam LocalDate date) {
        WeekReservationListResponseDto weekReservationListResponseDto =
                reservationQueryService.getWeeklyReservations(
                    user.getUserId(), date);
        return ResponseEntity.ok(weekReservationListResponseDto);
    }

    // 예약 가능 시간대 조회
    @PreAuthorize("hasAnyRole('GENERAL', 'MANAGER')")
    // TODO : 가능 시간대랑 불가능 시간대 같이 묶어보내줘야 하나 아니면 각자
    @GetMapping("/{assetId}/times")
    public ResponseEntity<AssetTimeResponseDto> getAssetTimes(
        @AuthenticationPrincipal CustomUserDetails user,
        @RequestParam Long assetId,
        @RequestParam LocalDate date) {
        AssetTimeResponseDto assetTimeResponseDto = reservationQueryService.getAssetTimes(
            user.getUserId(), assetId, date);
        return ResponseEntity.ok(assetTimeResponseDto);
    }
}
