package com.beyond.qiin.domain.booking.reservation.exception;

import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ReservationErrorCode implements ErrorCode {
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "해당 id의 예약 없습니다."),
  RESERVE_TIME_DUPLICATED(HttpStatus.CONFLICT, "RESERVATION_TIME_CONFLICT", "예약 시간이 다른 예약과 중복됩니다."),
  RESERVATION_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "RESERVATION_CANCEL_NOT_ALLOWED", "예약 시간 30분 전에만 취소 가능합니다.");

  private final HttpStatus status;
  private final String error;
  private final String message;

  ReservationErrorCode(final HttpStatus status, final String error, final String message) {
    this.status = status;
    this.error = error;
    this.message = message;
  }
}

