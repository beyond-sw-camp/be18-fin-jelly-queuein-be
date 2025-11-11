package com.beyond.qiin.booking.reservation.service.command;

import com.beyond.qiin.booking.dto.reservation.request.CreateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.request.UpdateReservationRequestDto;
import com.beyond.qiin.booking.dto.reservation.response.CreateReservationResponseDto;
import java.time.Instant;

public interface ReservationCommandService {

  CreateReservationResponseDto applyReservation(Long assetId, CreateReservationRequestDto createReservationRequestDto);
  CreateReservationResponseDto instantConfirmReservation(Long assetId, CreateReservationRequestDto createReservationRequestDto);
  void approveReservation(Long reservationId);
  void rejectReservation(Long reservationId);
  void updateReservation(Long reservationId, UpdateReservationRequestDto createReservationRequestDto);
  void startUsingReservation(Long reservationId, Instant startAt);
  void endUsingReservation(Long reservationId, Instant endAt);
  void cancelReservation(Long reservationId);

}
