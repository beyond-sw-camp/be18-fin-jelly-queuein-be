package com.beyond.qiin.domain.booking.reservation.service.query;

import com.beyond.qiin.common.dto.PageResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetListResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryService {
    Reservation getReservationById(final Long id);

    ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId);

    PageResponseDto<GetUserReservationResponseDto> getReservationsByUserId(
        final Long userId, final LocalDate date, final Pageable pageable);

    PageResponseDto<ReservableAssetResponseDto> getReservableAssets(
        final Long userId, final LocalDate date, Pageable pageable);

    AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date);

    WeekReservationListResponseDto getWeeklyReservations(
        final Long userId, final LocalDate date);

    MonthReservationListResponseDto getMonthlyReservations(
        final Long userId, final LocalDate month);

    PageResponseDto<GetAppliedReservationResponseDto> getReservationApplies(
            final Long userId, final LocalDate date, Pageable pageable);

    List<Reservation> getReservationsByUserAndDate(final Long userId, final LocalDate date, final Pageable pageable);

    List<Reservation> getReservationsByAssetAndDate(final Long userId, final Long assetId, final LocalDate date);

    List<Reservation> getReservationsByUserAndYearMonth(final Long userId, final LocalDate month);

    List<Reservation> getReservationsByUserAndWeek(final Long userId, final LocalDate date);

    List<Reservation> getReservationsPendingAndDate(final Long userId, final LocalDate date, Pageable pageabl);

    List<Reservation> getReservationsByAssetId(final Long assetId);
}
