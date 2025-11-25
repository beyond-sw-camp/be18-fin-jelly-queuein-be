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

    public static RoleException roleAlreadyExists() {
        return new RoleException(RoleErrorCode.ROLE_ALREADY_EXISTS);
    }

    public static RoleException roleCannotDeleteMaster() {
        return new RoleException(RoleErrorCode.ROLE_CANNOT_DELETE_MASTER);
    }

    public static RoleException systemRoleProtected() {
        return new RoleException(RoleErrorCode.SYSTEM_ROLE_PROTECTED);
    }

    @Getter
    public enum RoleErrorCode implements ErrorCode {
        ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_NOT_FOUND", "해당 역할을 찾을 수 없습니다."),
        ROLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "ROLE_ALREADY_EXISTS", "이미 존재하는 역할입니다."),
        ROLE_CANNOT_DELETE_MASTER(HttpStatus.FORBIDDEN, "ROLE_CANNOT_DELETE_MASTER", "MASTER 역할은 삭제할 수 없습니다."),
        SYSTEM_ROLE_PROTECTED(HttpStatus.FORBIDDEN, "SYSTEM_ROLE_PROTECTED", "시스템 기본 역할은 수정하거나 삭제할 수 없습니다.");

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
