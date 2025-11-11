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
//예약 생성 시에 대한 응답용
public class CreateReservationResponseDto {

  //예약 id
  @NotNull
  private Long reservationId;

  //신청자 이름
  @NotNull
  private String applicantName;

  //자원 이름
  private String assetName;

  //예약 시작 시간
  @NotNull
  private Instant startAt;

  //예약 종료 시간
  @NotNull
  private Instant endAt;

  //실제 사용 시간
  private Instant actualStartAt;

  //실제 종료 시간
  private Instant actualEndAt;

  private String description;

  private String reason;

  //버전을 넘겨야하나

  //승인 여부
  private String isApproved;

  //참여자들

  //예약 상태 - 숫자로 넘길 것인가 -> 숫자로 넘기지 않으면 어디서는 enum으로 처리하게 되는 것과 동일한데
  //문자열로 주지 않으면 프론트에서 그 값을 알고 처리해야하는 단점
  @NotNull
  private String status;


}
