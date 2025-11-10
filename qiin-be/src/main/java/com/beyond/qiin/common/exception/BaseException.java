package com.beyond.qiin.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

  private final ErrorCode errorCode;

  // 기본 예외처리
  protected BaseException(final ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  // 도메인별 예외처리 시 사용
  protected BaseException(final ErrorCode errorCode, final String message) {
    super(message);
    this.errorCode = errorCode;
  }
}
