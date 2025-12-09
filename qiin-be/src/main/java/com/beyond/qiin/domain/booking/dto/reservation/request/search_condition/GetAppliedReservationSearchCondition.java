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

    private String assetName; // 검색용이므로 unique 안하고 여러개 떠도 됨

    private String assetType;
    private Long categoryId;
    private String assetStatus;
    private String layerZero;
    private String layerOne;
}
