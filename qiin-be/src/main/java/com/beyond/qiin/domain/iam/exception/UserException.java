package com.beyond.qiin.domain.iam.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class UserException extends BaseException {

    public UserException(ErrorCode code) {
        super(code);
    }

    // 유저 에러 정의
    public static UserException userNotFound() {
        return new UserException(UserErrorCode.USER_NOT_FOUND);
    }

    public static UserException userDuplicateName() {
        return new UserException(UserErrorCode.USER_DUPLICATE_NAME);
    }

    // ErrorCode 내부에 정의
    @Getter
    public enum UserErrorCode implements ErrorCode {
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "해당 직원을 찾을 수 없습니다."),
        USER_DUPLICATE_NAME(HttpStatus.CONFLICT, "USER_DUPLICATE_NAME", "이미 존재하는 사원명입니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        UserErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
