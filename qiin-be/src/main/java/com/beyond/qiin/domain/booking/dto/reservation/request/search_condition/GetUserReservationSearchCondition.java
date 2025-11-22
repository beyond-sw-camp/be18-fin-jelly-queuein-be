package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
