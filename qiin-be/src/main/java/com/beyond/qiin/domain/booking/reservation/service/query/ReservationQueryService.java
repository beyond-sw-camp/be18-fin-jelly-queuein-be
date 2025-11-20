package com.beyond.qiin.domain.booking.reservation.service.query;

import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReservationQueryService {
    Reservation getReservationById(final Long id);

    ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId);

    GetUserReservationListResponseDto getReservationsByUserId(final Long userId, final LocalDate date);

    ReservableAssetListResponseDto getReservableAssets(final Long userId, final LocalDate date);

    AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date);

    WeekReservationListResponseDto getWeeklyReservations(final Long userId, final LocalDate date);

    MonthReservationListResponseDto getMonthlyReservations(final Long userId, final YearMonth yearMonth);

    GetAppliedReservationListResponseDto getReservationApplies(final Long userId, final LocalDate date);

    List<Reservation> getReservationsByUserAndDate(final Long userId, final LocalDate date);

    List<Reservation> getReservationsByAssetAndDate(final Long userId, final Long assetId, final LocalDate date);

    List<Reservation> getReservationsByUserAndYearMonth(final Long userId, final YearMonth yearMonth);

    List<Reservation> getReservationsPendingAndDate(final LocalDate date);

    List<Reservation> getReservationsByAssetId(final Long assetId);
}
