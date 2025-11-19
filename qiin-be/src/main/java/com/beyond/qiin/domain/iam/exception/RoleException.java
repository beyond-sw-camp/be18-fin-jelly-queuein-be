package com.beyond.qiin.domain.iam.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class RoleException extends BaseException {

    private RoleException(final RoleErrorCode code) {
        super(code);
    }

    public static RoleException roleNotFound() {
        return new RoleException(RoleErrorCode.ROLE_NOT_FOUND);
    }

    @Getter
    public enum RoleErrorCode implements ErrorCode {
        ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "해당 역할을 찾을 수 없습니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        RoleErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
