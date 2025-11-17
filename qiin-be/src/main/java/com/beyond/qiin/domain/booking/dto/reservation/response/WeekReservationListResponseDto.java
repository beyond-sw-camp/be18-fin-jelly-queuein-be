package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeekReservationListResponseDto {

  @Builder.Default
  private final List<WeekReservationResponseDto> reservations = new ArrayList<>();
}
