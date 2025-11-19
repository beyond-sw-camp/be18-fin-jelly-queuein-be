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
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponseDto> getReservation(@PathVariable Long reservationId) {
        ReservationDetailResponseDto reservationDetailResponseDto =
                reservationQueryService.getReservation(reservationId);
        return ResponseEntity.ok(reservationDetailResponseDto);
    }

    // 사용자의 예약한 자원에 대한 목록 조회
    @GetMapping("/me")
    public ResponseEntity<PageResponseDto<GetUserReservationResponseDto>> getUserReservations(
            @PathVariable Long userId, @RequestParam LocalDate date, Pageable pageable) {

        Page<GetUserReservationResponseDto> page =
                reservationQueryService.getReservationsByUserId(userId, date, pageable);

        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 예약 가능 자원 목록 조회
    @GetMapping("/{assetId}")
    public ResponseEntity<PageResponseDto<ReservableAssetResponseDto>> getReservableAssets(
            @RequestParam LocalDate date, Pageable pageable) {
        Page<GetUserReservationResponseDto> page = reservationQueryService.getReservableAssets(date, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 예약 신청 목록 조회(관리자용)
    @GetMapping("/pending")
    public ResponseEntity<PageResponseDto<GetAppliedReservationResponseDto>> getAppliedReservations(
            @RequestParam LocalDate date, Pageable pageable) {
        Page<GetAppliedReservationResponseDto> page = reservationQueryService.getReservationApplies(date, pageable);
        return ResponseEntity.ok(PageResponseDto.from(page));
    }

    // 월별 일정 조회
    @GetMapping("/monthly")
    public ResponseEntity<MonthReservationListResponseDto> getMonthlyReservations(
            @PathVariable Long userId, @RequestParam YearMonth yearMonth) {
        MonthReservationListResponseDto monthReservationListResponseDto =
                reservationQueryService.getMonthlyReservations(userId, yearMonth);
        return ResponseEntity.ok(monthReservationListResponseDto);
    }

    // 주별 일정 조회
    @GetMapping("/weekly")
    public ResponseEntity<WeekReservationListResponseDto> getWeeklyReservations(
            @PathVariable Long userId, @RequestParam LocalDate date) {
        WeekReservationListResponseDto weekReservationListResponseDto =
                reservationQueryService.getWeeklyReservations(userId, date);
        return ResponseEntity.ok(weekReservationListResponseDto);
    }

    // 예약 가능 시간대 조회
    // TODO : 가능 시간대랑 불가능 시간대 같이 묶어보내줘야 하나 아니면 각자
    @GetMapping("/{assetId}/times")
    public ResponseEntity<AssetTimeResponseDto> getAssetTimes(
            @RequestParam Long assetId, @RequestParam LocalDate date) {
        AssetTimeResponseDto assetTimeResponseDto = reservationQueryService.getAssetTimes(assetId, date);
        return ResponseEntity.ok(assetTimeResponseDto);
    }
}
