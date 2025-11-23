package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RawAppliedReservationResponseDto {

    private final Long assetId;
    private final String assetName;
    private final Long reservationId;
    private final String applicantName;
    private final String respondentName;
    private final String reservationStatus;
    private final Boolean isApproved;
    // TODO : status로 생각하기엔 말그대로 승인 / 거절
    // 근데 isApproved -> 이미 거절된 것으로 보일 수 있기 때문에 사실상 entity에 Boolean이어야하지 않나 싶네
    private final String reason;
}
