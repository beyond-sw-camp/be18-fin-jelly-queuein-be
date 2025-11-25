package com.beyond.qiin.domain.booking.util;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.vo.TimeSlot;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AvailableTimeSlotCalculator {

    public static boolean isReservable(List<Reservation> reservations, LocalDate date, ZoneId zoneId) {

        return !calculateAvailableSlots(reservations, date, zoneId).isEmpty();
    }

    public static List<TimeSlot> calculateAvailableSlots(
            List<Reservation> reservations, LocalDate date, ZoneId zoneId) {

        Instant startOfDay = date.atStartOfDay(zoneId).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant();

        // 실제 공간을 차지하는 예약만 차단
        List<Integer> blockingStatuses = List.of(1, 2, 5);
        // APPROVED(1), USING(2), COMPLETED(5)

        List<Reservation> filtered = reservations.stream()
                .filter(r -> blockingStatuses.contains(r.getStatus().getCode()))
                .sorted(Comparator.comparing(Reservation::getStartAt))
                .toList();

        List<TimeSlot> result = new ArrayList<>();

        Instant current = startOfDay;

        for (Reservation reservation : filtered) {

            Instant resStart = reservation.getStartAt();
            Instant resEnd = reservation.getEndAt();

            // 날짜 범위와 겹치지 않으면 패스
            if (resEnd.isBefore(startOfDay) || resStart.isAfter(endOfDay)) {
                continue;
            }

            // 하루 범위 안으로 조정
            resStart = resStart.isBefore(startOfDay) ? startOfDay : resStart;
            resEnd = resEnd.isAfter(endOfDay) ? endOfDay : resEnd;

            // 이전 예약 끝과 이번 예약 시작 사이에 빈 구간 있으면 available=true
            if (current.isBefore(resStart)) {
                result.add(TimeSlot.create(current, resStart, true));
            }

            // current 갱신
            if (resEnd.isAfter(current)) {
                current = resEnd;
            }
        }

        // 마지막 구간
        if (current.isBefore(endOfDay)) {
            result.add(TimeSlot.create(current, endOfDay, true));
        }

        return result;
    }
}
