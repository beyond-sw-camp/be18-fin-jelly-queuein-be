package com.beyond.qiin.booking.dto.reservation.response;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//캘린더, 일정표 용도
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DateReservationResponseListDto {

  @Builder.Default
  private ArrayList<DateReservationResponseDto> reservationResponseDtos = new ArrayList<>();
}
