package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetAppliedReservationSearchCondition {
    @NotNull
    private LocalDate date;

    private String applicantName;
    private String respondentName;
    private String isApproved;
    private String isReservable;

    @NotNull
    private String assetName; // TODO : assetName이 unique가 안되면 id로 변경 필요

    private String assetType;
    private String categoryName;
    private String assetStatus;
    private String layerZero;
    private String layerOne;
}
