package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
//TODO : ui 테이블에 필요없는 값들 제외
@Getter
@AllArgsConstructor
public class RawUserReservationResponseDto {
    private final String reservationId;

    private final String assetType;

    private final String assetName;

    private final String categoryName;

    private final String assetStatus;

    private final Boolean isApproved;

    private final Instant startAt;

    private final Instant endAt;

    private final String reservationStatus;

    private final Instant actualStartAt;

    private final Instant actualEndAt;

}
