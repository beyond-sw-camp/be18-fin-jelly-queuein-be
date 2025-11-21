package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.GetAppliedReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.request.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetAppliedReservationResponseDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservableAssetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppliedReservationsQueryAdapter {
  Page<GetAppliedReservationResponseDto> search(
      GetAppliedReservationSearchCondition condition,
      Pageable pageable);
}
