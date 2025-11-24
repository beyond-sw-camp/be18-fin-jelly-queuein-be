package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 예약 가능 자원 조회 시
@Getter
@Setter
@NoArgsConstructor
public class ReservableAssetSearchCondition {
    private LocalDate date;

    private String assetName;
    private String assetType;
    private String categoryName;
    private String assetStatus;
    private String layerZero;
    private String layerOne;
}
