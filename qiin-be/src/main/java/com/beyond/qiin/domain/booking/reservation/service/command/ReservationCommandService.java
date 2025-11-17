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
            Long assetId, CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto instantConfirmReservation(
            Long assetId, CreateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto approveReservation(final Long reservationId, ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto rejectReservation(final Long reservationId, ConfirmReservationRequestDto confirmReservationRequestDto);

    ReservationResponseDto updateReservation(Long reservationId, UpdateReservationRequestDto createReservationRequestDto);

    ReservationResponseDto startUsingReservation(Long reservationId, Instant startAt);

    ReservationResponseDto endUsingReservation(Long reservationId, Instant endAt);

    ReservationResponseDto cancelReservation(Long reservationId);

    Reservation getReservationById(Long id);

    List<Reservation> getReservationsByAssetId(Long assetId);
}
