package com.beyond.qiin.domain.booking.support;

import com.beyond.qiin.domain.booking.entity.Reservation;
import com.beyond.qiin.domain.booking.exception.ReservationErrorCode;
import com.beyond.qiin.domain.booking.exception.ReservationException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationValidator {

    private final ReservationReader reservationReader;

    // api x 비즈니스 메서드
    public void validateReservationAvailability(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {
        if (!isReservationTimeAvailable(reservationId, assetId, startAt, endAt))
            throw new ReservationException(ReservationErrorCode.RESERVE_TIME_DUPLICATED);
    }

    public void validateReservationCanceling(final Reservation reservation) {
        if (!isReservationCancelAvailable(reservation))
            // ddd -> 검증 / service 의 행동 결정(메시지 던짐)
            throw new ReservationException(ReservationErrorCode.RESERVATION_CANCEL_NOT_ALLOWED);
    }

    // test 가능하도록 package private 허용
    // 자원에 대한 예약 가능의 유무 -  비즈니스 책임이므로 command service로
    public boolean isReservationTimeAvailable(
            final Long reservationId, final Long assetId, final Instant startAt, final Instant endAt) {

        List<Reservation> reservations = reservationReader.getActiveReservationsByAssetId(assetId);

        for (Reservation reservation : reservations) {

            if (reservationId != null) { // 생성시는 null
                if (reservation.getId().equals(reservationId)) {
                    continue;
                }
            }

            Instant existingStart = reservation.getStartAt();
            Instant existingEnd = reservation.getEndAt();

            // 딱 맞닿는 경우는 허용
            if (startAt.equals(existingEnd) || endAt.equals(existingStart)) {
                continue;
            }

            // 겹침 체크
            boolean overlaps = startAt.isBefore(existingEnd) && endAt.isAfter(existingStart);

            if (overlaps) return false;
        }

        return true;
    }

    public boolean isReservationCancelAvailable(final Reservation reservation) {
        Instant now = Instant.now();
        Instant deadline = reservation.getStartAt().minus(30, ChronoUnit.MINUTES);

        if (now.isBefore(deadline)) {
            return true; // 취소 가능
        }
        return false;
    }
}
