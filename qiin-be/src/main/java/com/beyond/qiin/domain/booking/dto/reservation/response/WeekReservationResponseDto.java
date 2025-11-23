package com.beyond.qiin.domain.booking.dto.reservation.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WeekReservationResponseDto {

    private final LocalDate date;

    @Builder.Default
    private final List<MonthReservationDailyResponseDto> reservations = new ArrayList<>();
}
