package com.beyond.qiin.domain.booking.reservation.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;

public class ReservationException extends BaseException {
  public ReservationException(final ReservationErrorCode errorCode) {
    super(errorCode);
  }

  // 도메인별 예외처리 시 사용
  public ReservationException(final ReservationErrorCode errorCode, final String message) {
    super(errorCode, message);
  }
}
