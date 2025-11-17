package com.beyond.qiin.infra.kafka.reservation;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationResponseDto {
  private final Long reservationId;
  private final Instant startAt;
  private final Instant endAt;
  private final int status;
  private final Long version;
  private final boolean isApproved;

  public static ReservationResponseDto fromEntity(Reservation reservation) {
    return ReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .startAt(reservation.getStartAt())
        .endAt(reservation.getEndAt())
        .status(reservation.getStatus())
        .version(reservation.getVersion())
        .isApproved(reservation.isApproved())
        .build();
  }
}
