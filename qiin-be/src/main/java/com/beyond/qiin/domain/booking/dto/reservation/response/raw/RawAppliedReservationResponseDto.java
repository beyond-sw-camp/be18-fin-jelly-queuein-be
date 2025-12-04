package com.beyond.qiin.domain.booking.dto.reservation.response.raw;

import java.time.Instant;
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
    private final int reservationStatus;
    private final Boolean isApproved;
    private final String reason;
    private final Long version;
    private final Instant startAt;
    private final Instant endAt;
}
