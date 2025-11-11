package com.beyond.qiin.booking.dto.reservation.response;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//사용자의 예약에 대한 현황 목록 조회용
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetUserReservationResponseDto {

  @NotNull
  private String reservationId;

  @NotNull
  private String assetType;

  @NotNull
  private String assetName;

  @NotNull
  private String categoryName;

  @NotNull
  private String assetStatus;

  @NotNull
  private Boolean isApproved;

  @NotNull
  private Instant startAt;

  @NotNull
  private Instant endAt;

  private Instant actualStartAt;

  private Instant actualEndAt;
}
