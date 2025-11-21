package com.beyond.qiin.domain.booking.dto.reservation.request;

import java.time.Instant;
import lombok.Getter;

@Getter
public class GetUserReservationSearchCondition {
  private Instant date;

  private String reservationStatus;
  private String isApproved;

  private String assetName;
  private String assetType;
  private String categoryName;
  private String assetStatus;
  private String layerZero;
  private String layerOne;
}
