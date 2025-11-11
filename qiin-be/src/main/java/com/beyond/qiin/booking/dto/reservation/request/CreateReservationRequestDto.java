package com.beyond.qiin.booking.dto.reservation.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CreateReservationRequestDto {

  //신청자
  @NotNull
  private String applicantName;

  //자원명
  @NotNull
  private String assetName;

  //예약 시작 시간
  @NotNull
  private Instant startAt;

  //예약 종료 시간
  @NotNull
  private Instant endAt;

  //예약 설명
  @NotNull
  private String description;

  //참여자들
}
