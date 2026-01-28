package com.beyond.qiin.domain.booking.dto.reservation.request.search_condition;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GetUserReservationSearchCondition {

    private LocalDate fromDate;

    private LocalDate toDate;

    private String reservationStatus;
    private String isApproved;
    private Long categoryId;
}
