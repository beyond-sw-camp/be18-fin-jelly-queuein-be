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
            final Long assetId, final CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto instantConfirmReservation(
            final Long assetId, final CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto approveReservation(
            final Long reservationId, final ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto rejectReservation(
            final Long reservationId, final ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto updateReservation(
            final Long reservationId, final UpdateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto startUsingReservation(final Long reservationId, final Instant startAt);

    ReservationResponseDto endUsingReservation(final Long reservationId, final Instant endAt);

    ReservationResponseDto cancelReservation(final Long reservationId);

    Reservation getReservationById(final Long id);

    List<Reservation> getReservationsByAssetId(final Long assetId);
}
