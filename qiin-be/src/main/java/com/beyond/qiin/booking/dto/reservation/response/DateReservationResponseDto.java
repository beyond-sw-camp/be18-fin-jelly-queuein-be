package com.beyond.qiin.booking.dto.reservation.response;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DateReservationResponseDto {

  @NotNull
  private Instant startAt;

  @NotNull
  private Instant endAt;

  @NotNull
  private String assetName;

  @NotNull
  private String month;

  @NotNull
  private String day;

}
