package com.beyond.qiin.domain.booking.dto.reservation.request;

import java.time.Instant;
import lombok.Getter;

// 예약 가능 자원 조회 시
@Getter
public class ReservableAssetSearchCondition {
  private Instant date;

  private String assetName;
  private String assetType;
  private String categoryName;
  private String assetStatus;
  private String layerZero;
  private String layerOne;
}
