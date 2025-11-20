package com.beyond.qiin.domain.booking.reservation.service.command;

import com.beyond.qiin.domain.booking.dto.reservation.request.ConfirmReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.domain.booking.dto.reservation.response.ReservationResponseDto;
import com.beyond.qiin.domain.booking.reservation.entity.Reservation;
import java.time.Instant;
import java.util.List;

public interface ReservationCommandService {

    ReservationResponseDto applyReservation(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto instantConfirmReservation(
            final Long userId, final Long assetId, final CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto approveReservation(
            final Long userId,
            final Long reservationId,
            final ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto rejectReservation(
            final Long userId,
            final Long reservationId,
            final ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto startUsingReservation(final Long userId, final Long reservationId, final Instant startAt);

    ReservationResponseDto endUsingReservation(final Long userId, final Long reservationId, final Instant endAt);

    ReservationResponseDto cancelReservation(final Long userId, final Long reservationId);

    ReservationResponseDto updateReservation(
            final Long userId, final Long reservationId, final UpdateReservationRequestDto createReservationRequestDto);

    Reservation getReservationById(final Long id);

    List<Reservation> getReservationsByAssetId(final Long assetId);
}
