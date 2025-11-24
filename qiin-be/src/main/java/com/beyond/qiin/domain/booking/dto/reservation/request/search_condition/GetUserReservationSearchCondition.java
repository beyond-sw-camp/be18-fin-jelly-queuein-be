package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserReservationSearchCondition {
    private LocalDate date;

    private String reservationStatus;
    private String isApproved;

    private String assetName;
    private String assetType;
    private String categoryName;
    private String assetStatus;
    private String layerZero;
    private String layerOne;
}
