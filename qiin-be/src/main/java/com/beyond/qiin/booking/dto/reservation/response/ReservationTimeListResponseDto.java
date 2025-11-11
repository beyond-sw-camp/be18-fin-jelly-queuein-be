package com.beyond.qiin.booking.dto.reservation.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
//자원의 전체 예약 가능 / 점유 시간 보는 용도
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationTimeListResponseDto {

  @Builder.Default
  List<ReservationTimeResponseDto> reservationTimeResponseDtoList = new ArrayList<>();
}
