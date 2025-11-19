package com.beyond.qiin.domain.iam.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class PermissionException extends BaseException {

    private PermissionException(final PermissionErrorCode code) {
        super(code);
    }

    public static PermissionException permissionNotFound() {
        return new PermissionException(PermissionErrorCode.PERMISSION_NOT_FOUND);
    }

    public static PermissionException permissionAlreadyExists() {
        return new PermissionException(PermissionErrorCode.PERMISSION_ALREADY_EXISTS);
    }

    public static PermissionException permissionInvalid() {
        return new PermissionException(PermissionErrorCode.PERMISSION_INVALID);
    }

    public static PermissionException permissionInUse() {
        return new PermissionException(PermissionErrorCode.PERMISSION_IN_USE);
    }

    // ErrorCode 내부에 정의
    @Getter
    public enum PermissionErrorCode implements ErrorCode {
        PERMISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "PERMISSION_NOT_FOUND", "해당 권한을 찾을 수 없습니다."),
        PERMISSION_ALREADY_EXISTS(HttpStatus.CONFLICT, "PERMISSION_ALREADY_EXISTS", "이미 존재하는 권한입니다."),
        PERMISSION_INVALID(HttpStatus.BAD_REQUEST, "PERMISSION_INVALID", "권한 값이 올바르지 않습니다."), // '예약 승인':O '예약승인':X
        PERMISSION_IN_USE(HttpStatus.CONFLICT, "PERMISSION_IN_USE", "다른 역할(Role)에서 이 권한을 사용 중이라 삭제할 수 없습니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        PermissionErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
