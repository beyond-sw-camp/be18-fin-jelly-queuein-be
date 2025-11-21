package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import java.time.Instant;
import lombok.Getter;

@Getter
public class GetAppliedReservationSearchCondition {
    private Instant date;

    private String applicantName;
    private String respondentName;
    private String isApproved;
    private String isReservable;

    private String assetName;
    private String assetType;
    private String categoryName;
    private String assetStatus;
    private String layerZero;
    private String layerOne;
}
