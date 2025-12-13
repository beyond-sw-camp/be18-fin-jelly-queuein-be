package com.beyond.qiin.domain.alarm.exception;

import com.beyond.qiin.common.exception.BaseException;

public class NotificationException extends BaseException {
    public NotificationException(final NotificationErrorCode errorCode) {
        super(errorCode);
    }

    // 도메인별 추가 메시지가 필요한 경우 사용
    public NotificationException(final NotificationErrorCode errorCode, final String message) {
        super(errorCode, message);
    }
}
