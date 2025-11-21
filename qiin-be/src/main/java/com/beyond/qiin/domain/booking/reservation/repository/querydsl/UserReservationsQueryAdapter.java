package com.beyond.qiin.domain.booking.reservation.repository.querydsl;

import com.beyond.qiin.domain.booking.dto.reservation.request.search_condition.GetUserReservationSearchCondition;
import com.beyond.qiin.domain.booking.dto.reservation.response.GetUserReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserReservationsQueryAdapter {
    Page<GetUserReservationResponseDto> search(
            Long userId,
        GetUserReservationSearchCondition condition,
        int reservationStatus,
        Pageable pageable);
}
