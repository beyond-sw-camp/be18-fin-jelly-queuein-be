package com.beyond.qiin.infra.kafka.reservation;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationRequestDto {
  @NotNull
  private final Instant startAt;   // not null

  @NotNull
  private final Instant endAt;     // not null

  private final String description;
}
