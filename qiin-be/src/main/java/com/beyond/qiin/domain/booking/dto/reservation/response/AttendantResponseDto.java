package com.beyond.qiin.domain.booking.dto.reservation.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendantResponseDto {

  private final Long attendantId;
  private final String attendantName;
}
