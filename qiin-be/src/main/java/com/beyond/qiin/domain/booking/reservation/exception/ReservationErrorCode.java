package com.beyond.qiin.domain.booking.reservation.exception;

import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReservationErrorCode implements ErrorCode {
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", "해당 id의 예약 없습니다."),
    RESERVE_TIME_DUPLICATED(HttpStatus.CONFLICT, "RESERVATION_TIME_CONFLICT", "예약 시간이 다른 예약과 중복됩니다."),
    RESERVATION_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "RESERVATION_CANCEL_NOT_ALLOWED", "예약 시간 30분 전에만 취소 가능합니다."),
    RESERVATION_STATUS_CHANGE_NOT_ALLOWED(
            HttpStatus.BAD_REQUEST, "RESERVATION_CHANGE_NOT_ALLOWED", "예약 상태에 대한 변경 순서가 잘못되었습니다."),
    USING_RESERVATION_NOT_DELETED(
            HttpStatus.BAD_REQUEST, "USING_RESERVATION_NOT_DELETED", "사용중인 자원은 삭제 불가합니다. 사용 완료하세요."),
    RESERVATION_REQUEST_DUPLICATED(HttpStatus.CONFLICT, "RESERVATION_REQUEST_DUPLICATED", "자원 예약을 위해 대기중입니다."),
    RESERVATION_CREATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "RESERVATION_CREATE_FAILED", "자원에 대한 예약을 실패했습니다.");

    private final HttpStatus status;
    private final String error;
    private final String message;

    ReservationErrorCode(final HttpStatus status, final String error, final String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
