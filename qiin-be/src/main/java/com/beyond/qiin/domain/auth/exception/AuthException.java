package com.beyond.qiin.domain.auth.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AuthException extends BaseException {

    private AuthException(final AuthErrorCode code) {
        super(code);
    }

    public static AuthException unauthorized() {
        return new AuthException(AuthErrorCode.UNAUTHORIZED);
    }

    public static AuthException tokenExpired() {
        return new AuthException(AuthErrorCode.TOKEN_EXPIRED);
    }

    @Getter
    public enum AuthErrorCode implements ErrorCode {
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다."),
        TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다.");

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
