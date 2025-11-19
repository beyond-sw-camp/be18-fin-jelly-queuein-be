package com.beyond.qiin.domain.booking.reservation.vo;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TimeSlot {
    private final Instant start;
    private final Instant end;
    private final boolean available;

    public static TimeSlot create(Instant start, Instant end, boolean available) {
        return TimeSlot.builder().start(start).end(end).available(available).build();
    }
}
