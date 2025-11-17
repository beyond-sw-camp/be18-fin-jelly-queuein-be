package com.beyond.qiin.domain.booking.reservation.service.query;

import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReservationQueryService {
    Reservation getReservationById(Long id);

    GetUserReservationListResponseDto getReservationsByUserId(Long userId, LocalDate date);

    ReservableAssetListResponseDto getReservableAssets(LocalDate date);

    ReservationDetailResponseDto getReservation(Long id);

    ReservableAssetTimeResponseDto getReservableAssetTimes(Long assetId, LocalDate date);

    WeekReservationListResponseDto getWeeklyReservations(Long userId, LocalDate date);

    MonthReservationListResponseDto getMonthlyReservations(Long userId, YearMonth yearMonth);

    GetAppliedReservationListResponseDto getReservationApplies(LocalDate date);

    List<Reservation> getReservationsByUserAndDate(Long userId, LocalDate date);

    List<Reservation> getReservationsByAssetAndDate(Long assetId, LocalDate date);

    List<Reservation> getReservationsByUserAndYearMonth(Long userId, YearMonth yearMonth);

    List<Reservation> getReservationsPendingAndDate(LocalDate date);
}
