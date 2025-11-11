package com.beyond.qiin.booking.dto.reservation.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UpdateReservationRequestDto {
  private String description;

  private List<String> participants;
}
