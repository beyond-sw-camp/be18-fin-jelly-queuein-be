package com.beyond.qiin.domain.booking.exception;

import com.beyond.qiin.common.exception.BaseException;

public class WaitingQueueException extends BaseException {

    public WaitingQueueException(final WaitingQueueErrorCode errorCode) {
        super(errorCode);
    }

    // 도메인별 예외처리 시 사용
    public WaitingQueueException(final WaitingQueueErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
