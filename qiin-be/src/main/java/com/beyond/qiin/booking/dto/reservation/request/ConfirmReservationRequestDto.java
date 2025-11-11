package com.beyond.qiin.booking.dto.reservation.request;

import jakarta.validation.constraints.NotNull;

public class ConfirmReservationRequestDto {

  @NotNull
  private String respondentName;

  private String reason;
}
