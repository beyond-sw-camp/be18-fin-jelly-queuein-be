package com.beyond.qiin.domain.booking.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.ReservableAssetSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.raw.RawReservableAssetResponseDto;
import java.util.List;

public interface ReservableAssetsQueryRepository {
    List<RawReservableAssetResponseDto> search(ReservableAssetSearchCondition condition);
}
