 package com.beyond.qiin.domain.booking.reservation.service.query;

 import com.beyond.qiin.domain.booking.dto.reservation.response.AssetTimeResponseDto;
 import com.beyond.qiin.domain.booking.dto.reservation.response.MonthReservationListResponseDto;
 import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
 import com.beyond.qiin.domain.booking.dto.reservation.response.WeekReservationListResponseDto;
 import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
 import java.time.Instant;
 import java.time.LocalDate;
 import java.util.List;

 public interface ReservationQueryService {
    Reservation getReservationById(final Long id);

    ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId);

//    PageResponseDto<GetUserReservationResponseDto> getReservationsByUserId(
//            final Long userId, final GetUserReservationSearchCondition condition, final Pageable pageable);
//
//    PageResponseDto<ReservableAssetResponseDto> getReservableAssets(
//            final Long userId, final ReservableAssetSearchCondition condition, Pageable pageable);
//
//   PageResponseDto<GetAppliedReservationResponseDto> getReservationApplies(
//       final Long userId, final GetAppliedReservationSearchCondition condition, Pageable pageable);
//

   AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date);

    WeekReservationListResponseDto getWeeklyReservations(final Long userId, Instant start, Instant end);

    MonthReservationListResponseDto getMonthlyReservations(final Long userId, final Instant from, final Instant to);

    List<Reservation> getReservationsByUserAndYearMonth(final Long userId, final Instant start, final Instant end);

    List<Reservation> getReservationsByUserAndWeek(final Long userId, final Instant start, final Instant end);

    List<Reservation> getReservationsByAssetId(final Long assetId);

    List<Reservation> getReservationsByAssetAndDate(final Long assetId, final LocalDate date);

    //    List<Reservation> getReservationsPendingAndDate(final Long userId, final LocalDate date, Pageable pageable);
    //
    //    List<Reservation> getReservationsByUserAndDate(final Long userId, final LocalDate date, final Pageable
    // pageable);

 }
