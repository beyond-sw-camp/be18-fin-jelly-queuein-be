package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
// TODO : ui 테이블에 필요없는 값들 제외

@Getter
@AllArgsConstructor
public class RawUserReservationResponseDto {
    private final Long reservationId;
    private final Instant startAt;
    private final Instant endAt;
    private final int reservationStatus;
    private final boolean isApproved;
    private final Instant actualStartAt;
    private final Instant actualEndAt;
    private final Long version;
    private final Long assetId;
    private final String assetName;
    private final String categoryName;
    private final int assetType;
    private final int assetStatus;
}
