package com.beyond.qiin.domain.notification.exception;

import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "해당 id의 알림이 존재하지 않습니다.");

    private final HttpStatus status;
    private final String error;
    private final String message;

    NotificationErrorCode(final HttpStatus status, final String error, final String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
