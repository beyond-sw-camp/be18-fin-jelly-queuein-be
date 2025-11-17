package com.beyond.qiin.domain.booking.reservation.util;

import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import com.beyond.qiin.domain.booking.reservation.vo.TimeSlot;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AvailableTimeSlotCalculator {

  public static boolean isReservable(
      List<Reservation> reservations,
      LocalDate date,
      ZoneId zoneId){

    if(calculateAvailableSlots(reservations, date, zoneId).isEmpty()){
      return false;
    }
    return true;
  }

  public static List<TimeSlot> calculateAvailableSlots(
      List<Reservation> reservations, //전체 예약
      LocalDate date, //해당 날짜
      ZoneId zoneId //time zone
  ) {

    Instant startOfDay = date.atStartOfDay(zoneId).toInstant();
    Instant endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant(); // 24:00

    // 예약 시간을 시작 기준으로 정렬
    List<Reservation> sorted = reservations.stream()
        .sorted(Comparator.comparing(Reservation::getStartAt))
        .toList();

    List<TimeSlot> result = new ArrayList<>(); //가능 시간

    Instant current = startOfDay; //현재 시각

    for (Reservation reservation : sorted) {
      //해당 예약의 시간
      Instant resStart = reservation.getStartAt();
      Instant resEnd = reservation.getEndAt();

      // 현재 시각 < 예약 시각이라면 빈 공간 존재
      if (current.isBefore(resStart)) {
        result.add(TimeSlot.create(current, resStart));
      }

      // 다음 비교 기준을 예약 끝으로 업데이트
      if (current.isBefore(resEnd)) {
        current = resEnd;
      }
    }

    // 마지막 구간 처리
    if (current.isBefore(endOfDay)) {
      result.add(TimeSlot.create(current, endOfDay));
    }

    if(reservations.isEmpty()){
      return Collections.emptyList();
    }
    return result;
  }


}
