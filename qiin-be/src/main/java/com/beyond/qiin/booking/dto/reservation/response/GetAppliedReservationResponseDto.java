package com.beyond.qiin.booking.dto.reservation.response;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

//관리자 승인 / 거절
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class GetAppliedReservationResponseDto {

  //자원명
  @NotNull
  private String assetName;

  //승인 필요 유무
  @NotNull
  private Long reservationId;

  //신청자
  @NotNull
  private String applicantName;

  //예약 가능 유무
  @NotNull
  private Boolean isReservable;

  //승인 여부
  @NotNull
  private String isApproved;

  //사유
  private String reason;

}
