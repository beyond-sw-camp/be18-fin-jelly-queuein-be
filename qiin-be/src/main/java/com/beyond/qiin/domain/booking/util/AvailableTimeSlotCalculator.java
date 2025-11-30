package com.beyond.qiin.domain.booking.util;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.vo.TimeSlot;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class AvailableTimeSlotCalculator {

    public static boolean isReservable(List<Reservation> reservations, LocalDate date, ZoneId zoneId) {

        return !calculateAvailableSlots(reservations, date, zoneId).isEmpty();
    }

    public static List<TimeSlot> calculateAvailableSlots(
        List<Reservation> reservations, LocalDate date, ZoneId zoneId) {

        List<TimeSlot> result = new ArrayList<>();

        for (int h = 0; h < 24; h++) {

            Instant blockStart = date.atTime(h, 0).atZone(zoneId).toInstant();
            Instant blockEnd = (h == 23)
                ? date.plusDays(1).atTime(0,0).atZone(zoneId).toInstant()
                : date.atTime(h+1, 0).atZone(zoneId).toInstant();

            boolean available = true;

            for (Reservation r : reservations) {
                if (r.getStartAt().isBefore(blockEnd) &&
                    r.getEndAt().isAfter(blockStart)) {

                    available = false;
                    break;
                }
            }

            result.add(TimeSlot.create(blockStart, blockEnd, available));
        }

        return result;
    }

}
