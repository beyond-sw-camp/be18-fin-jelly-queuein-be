package com.beyond.qiin.domain.auth.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * AuthErrorCode + AuthException 을 한 파일에서 관리
 * 파일명: AuthException.java
 */
public class AuthException extends BaseException {

    public AuthException(final AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(final AuthErrorCode errorCode, final String message) {
        super(errorCode, message);
    }

    @Getter
    public enum AuthErrorCode implements ErrorCode {
        ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "요청한 역할(Role)을 찾을 수 없습니다."),
        USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        AuthErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
