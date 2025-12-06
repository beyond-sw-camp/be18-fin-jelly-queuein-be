package com.beyond.qiin.domain.booking.queue;

import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;

public interface ReservationLockService {
    ReservationResponseDto reserveReservationWithLock(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto);
}
