package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.RawReservableAssetResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservableAssetsQueryRepository {
    Page<RawReservableAssetResponseDto> search(ReservableAssetSearchCondition condition, Pageable pageable);
}
