package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.enums.ReservationStatus;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RawUserReservationResponseDto{
  private final String reservationId;

  private final String assetType;

  private final String assetName;

  private final String categoryName;

  private final String assetStatus;

  private final Boolean isApproved;

  private final Instant startAt;

  private final Instant endAt;

  private final String reservationStatus;

  private final Instant actualStartAt;

  private final Instant actualEndAt;

  public RawUserReservationResponseDto(
      Long reservationId,           // reservation.id
      Instant startAt,              // reservation.startAt
      Instant endAt,                // reservation.endAt
      Integer statusCode,           // reservation.status (int)
      Boolean isApproved,           // reservation.isApproved
      Long assetId,                 // asset.id (사용 안하지만 QueryDSL 맞추기용)
      String assetName,             // asset.name
      String categoryName,          // category.name
      Integer assetType,            // asset.type
      Integer assetStatus) {        // asset.status

    this.reservationId = reservationId.toString();
    this.startAt = startAt;
    this.endAt = endAt;

    // int → Enum → String
    this.reservationStatus = ReservationStatus.from(statusCode).name();

    this.isApproved = isApproved;

    this.assetName = assetName;
    this.categoryName = categoryName;

    // int → String
    this.assetType = assetType == null ? null : assetType.toString();
    this.assetStatus = assetStatus == null ? null : assetStatus.toString();

    // Projection에서는 actualStartAt / actualEndAt 없음 → null
    this.actualStartAt = null;
    this.actualEndAt = null;
  }
}

