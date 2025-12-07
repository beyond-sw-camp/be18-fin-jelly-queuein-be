package com.beyond.qiin.domain.waiting_queue.exception;

import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum WaitingQueueErrorCode implements ErrorCode {
    IN_WATING_QUEUE(HttpStatus.TOO_MANY_REQUESTS, "IN_WAITING_QUEUE", "현재 대기열에 있습니다."),
    WAITING_QUEUE_EXPIRED(HttpStatus.GONE, "WAITING_QUEUE_EXPIRED", "대기 토큰이 만료되었습니다.");

    private final HttpStatus status;
    private final String error;
    private final String message;

    WaitingQueueErrorCode(HttpStatus status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
