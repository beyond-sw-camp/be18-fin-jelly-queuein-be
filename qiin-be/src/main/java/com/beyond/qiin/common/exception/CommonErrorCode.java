package com.beyond.qiin.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommonErrorCode implements ErrorCode {

  BAD_REQUEST(HttpStatus.BAD_REQUEST,"BAD_REQUEST", "잘못된 요청입니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND,"NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR","서버 내부 오류가 발생했습니다."),
  FLYWAY_MIGRATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FLYWAY_MIGRATION_ERROR", "데이터베이스 마이그레이션 중 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String error;
  private final String message;

  CommonErrorCode(final HttpStatus status,final String error, final String message) {
    this.status = status;
    this.error = error;
    this.message = message;
  }
}
