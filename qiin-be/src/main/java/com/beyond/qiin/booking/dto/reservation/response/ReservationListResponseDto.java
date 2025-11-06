package com.beyond.qiin.booking.dto.reservation.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReservationListResponseDto {

  @Builder.Default
  List<ReservationResponseDto> reservationList = new ArrayList<>();
}
