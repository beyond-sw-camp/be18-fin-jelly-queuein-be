package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.vo.TimeSlot;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 가능한 시간
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class TimeSlotDto {
    private final String start; // "9:00"과 같은 형태로
    private final String end;

    public static TimeSlotDto create(TimeSlot timeSlot, String zone) {
        ZoneId zoneId = ZoneId.of(zone);

        return TimeSlotDto.builder()
                .start(timeSlot.getStart().atZone(zoneId).format(DateTimeFormatter.ofPattern("HH:mm")))
                .end(timeSlot.getEnd().atZone(zoneId).format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }
}
