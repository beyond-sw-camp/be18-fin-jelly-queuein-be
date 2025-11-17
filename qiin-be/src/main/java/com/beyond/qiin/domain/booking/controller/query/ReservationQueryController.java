package com.beyond.qiin.domain.booking.controller.query;

import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.service.query.ReservationQueryService;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
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

  // 사용자의 예약한 자원에 대한 목록 조회
  @GetMapping("/me")
  public ResponseEntity<GetUserReservationListResponseDto> getUserReservations(@PathVariable Long userId, @RequestParam LocalDate date) {
    GetUserReservationListResponseDto getUserReservationListResponseDto = reservationQueryService.getReservationsByUserId(userId, date);
    return ResponseEntity.ok(getUserReservationListResponseDto);
  }

  // 예약 상세 조회
  @GetMapping("/{reservationId}")
  public ResponseEntity<ReservationDetailResponseDto> getReservation(@PathVariable Long reservationId){
    ReservationDetailResponseDto reservationDetailResponseDto = reservationQueryService.getReservation(reservationId);
    return ResponseEntity.ok(reservationDetailResponseDto);
  }

  // 예약 가능 자원 조회
  @GetMapping("/{assetId}")
  public ResponseEntity<ReservableAssetListResponseDto> getReservableAssets(@RequestParam LocalDate date){
    ReservableAssetListResponseDto reservableAssetListResponseDto = reservationQueryService.getReservableAssets(date);
    return ResponseEntity.ok(reservableAssetListResponseDto);
  }

  //예약 가능 시간대 조회
  @GetMapping("/{assetId}/times")
  public ResponseEntity<ReservableAssetTimeResponseDto> getReservableAssetTimes(@RequestParam Long assetId, @RequestParam LocalDate date){
    ReservableAssetTimeResponseDto reservableAssetTimeResponseDto = reservationQueryService.getReservableAssetTimes(assetId, date);
    return ResponseEntity.ok(reservableAssetTimeResponseDto);

  }

  // 예약 신청 목록 조회(관리자용)
  @GetMapping("/pending")
  public ResponseEntity<GetAppliedReservationListResponseDto> getAppliedReservations(@RequestParam LocalDate date){
    GetAppliedReservationListResponseDto getAppliedReservationListResponseDto = reservationQueryService.getReservationApplies(date);
    return ResponseEntity.ok(getAppliedReservationListResponseDto);
  }

  // 월별 일정 조회
  @GetMapping("/monthly")
  public ResponseEntity<MonthReservationListResponseDto> getMonthlyReservations(@PathVariable Long userId, @RequestParam YearMonth yearMonth){
    MonthReservationListResponseDto monthReservationListResponseDto = reservationQueryService.getMonthlyReservations(userId, yearMonth);
    return ResponseEntity.ok(monthReservationListResponseDto);
  }

  // 주별 일정 조회
  @GetMapping("/weekly")
  public ResponseEntity<WeekReservationListResponseDto> getWeeklyReservations(@PathVariable Long userId, @RequestParam LocalDate date){
    WeekReservationListResponseDto weekReservationListResponseDto = reservationQueryService.getWeeklyReservations(userId, date);
    return ResponseEntity.ok(weekReservationListResponseDto);
  }
}
