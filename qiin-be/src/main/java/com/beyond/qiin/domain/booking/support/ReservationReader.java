package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import com.beyond.qiin.domain.booking.repository.ReservationJpaRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationReader {
    private final ReservationJpaRepository reservationJpaRepository;

    // 자원 자체 (예외처리 포함)
    public Reservation getReservationById(final Long id) {
        Reservation reservation = reservationJpaRepository
                .findById(id)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
        return reservation;
    }

    public List<Reservation> getReservationsByUserAndYearMonth(
            final Long userId, final Instant from, final Instant to) {

        List<Reservation> reservations = reservationJpaRepository.findByUserIdAndYearMonth(userId, from, to);
        return reservations;
    }

    public List<Reservation> getReservationsByUserAndWeek(final Long userId, final Instant start, final Instant end) {
        List<Reservation> reservations = reservationJpaRepository.findByUserIdAndWeek(userId, start, end);

        return reservations;
    }

    // 자원 목록 (예외처리 포함)
    public List<Reservation> getReservationsByAssetId(final Long assetId) {
        List<Reservation> reservations = reservationJpaRepository.findByAssetId(assetId);
        return reservations;
    }

    // 자원에 대한 해당 날짜의 예약
    public List<Reservation> getReservationsByAssetAndDate(final Long assetId, final LocalDate date) {

        ZoneId zone = ZoneId.of("Asia/Seoul");

        Instant startOfDay = date.atStartOfDay(zone).toInstant();
        Instant endOfDay = date.plusDays(1).atStartOfDay(zone).toInstant();

        return reservationJpaRepository.findAllByAssetIdAndDate(assetId, startOfDay, endOfDay);
    }
}
