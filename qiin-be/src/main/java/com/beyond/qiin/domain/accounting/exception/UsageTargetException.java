package com.beyond.qiin.domain.accounting.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class UsageTargetException extends BaseException {

    private UsageTargetException(final UsageTargetErrorCode code) {
        super(code);
    }

    /** 이미 올해 목표가 존재할 때 */
    public static UsageTargetException alreadyExists() {
        return new UsageTargetException(UsageTargetErrorCode.USAGE_TARGET_ALREADY_EXISTS);
    }

    /** 조회하려는 연도의 목표 사용률이 없을 때 */
    public static UsageTargetException notFound() {
        return new UsageTargetException(UsageTargetErrorCode.USAGE_TARGET_NOT_FOUND);
    }

    @Getter
    public enum UsageTargetErrorCode implements ErrorCode {
        USAGE_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "USAGE_TARGET_NOT_FOUND", "해당 연도의 목표 사용률을 찾을 수 없습니다."),

        USAGE_TARGET_ALREADY_EXISTS(HttpStatus.CONFLICT, "USAGE_TARGET_ALREADY_EXISTS", "이미 올해 목표 사용률이 등록되었습니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        UsageTargetErrorCode(HttpStatus status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
