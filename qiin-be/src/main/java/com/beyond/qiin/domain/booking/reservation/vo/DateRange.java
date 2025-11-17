package com.beyond.qiin.domain.booking.reservation.vo;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class DateRange {
    private final Instant startDay;
    private final Instant endDay;

    public static DateRange create(Instant startDay, Instant endDay) {
        return DateRange.builder().startDay(startDay).endDay(endDay).build();
    }
}
