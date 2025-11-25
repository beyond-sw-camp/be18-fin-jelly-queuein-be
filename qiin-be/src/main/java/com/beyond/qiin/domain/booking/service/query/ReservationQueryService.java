//package com.beyond.qiin.domain.booking.service.query;
//
//import com.beyond.qiin.common.dto.PageResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetAppliedReservationSearchCondition;
//import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
//import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
//import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationDetailResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.applied_reservation.GetAppliedReservationResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.asset_time.AssetTimeResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.month_reservation.MonthReservationListResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.reservable_asset.ReservableAssetResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.user_reservation.GetUserReservationResponseDto;
//import com.beyond.qiin.domain.booking.dto.reservation.response.week_reservation.WeekReservationListResponseDto;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import org.springframework.data.domain.Pageable;
//
//public interface ReservationQueryService {
//    ReservationDetailResponseDto getReservation(final Long userId, final Long reservationId);
//
//    PageResponseDto<GetUserReservationResponseDto> getReservationsByUserId(
//            final Long userId, final GetUserReservationSearchCondition condition, final Pageable pageable);
//
//    PageResponseDto<ReservableAssetResponseDto> getReservableAssets(
//            final Long userId, final ReservableAssetSearchCondition condition, Pageable pageable);
//
//    PageResponseDto<GetAppliedReservationResponseDto> getReservationApplies(
//            final Long userId, final GetAppliedReservationSearchCondition condition, Pageable pageable);
//
//    AssetTimeResponseDto getAssetTimes(final Long userId, final Long assetId, final LocalDate date);
//
//    WeekReservationListResponseDto getWeeklyReservations(final Long userId, final LocalDate date);
//
//    MonthReservationListResponseDto getMonthlyReservations(final Long userId, final YearMonth yearMonth);
//
//    //    List<Reservation> getReservationsPendingAndDate(final Long userId, final LocalDate date, Pageable pageable);
//
//    //    List<Reservation> getReservationsByUserAndDate(final Long userId, final LocalDate date, final Pageable
//    // pageable);
//
//}
