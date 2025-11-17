package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthReservationResponseDto {

  private final Long reservationId;

  private final Instant startAt;

  private final String assetName;

  public static MonthReservationResponseDto fromEntity(Reservation reservation){
    return MonthReservationResponseDto.builder()
        .reservationId(reservation.getId())
        .startAt(reservation.getStartAt())
        .assetName(reservation.getAsset().getName())
        .build();
  }
}
