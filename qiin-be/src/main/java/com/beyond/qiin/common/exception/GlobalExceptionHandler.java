package com.beyond.qiin.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.FlywayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponse> handleBaseException(final BaseException ex) {
    ErrorCode code = ex.getErrorCode();
    log.warn("[BusinessException] {} - {}", code.getError(), ex.getMessage());
    return ResponseEntity
        .status(code.getStatus())
        .body(ErrorResponse.of(code, ex.getMessage()));
  }

  // Flyway 마이그레이션 예외 (DB 버전 충돌, 스크립트 오류 등)
  @ExceptionHandler(FlywayException.class)
  public ResponseEntity<ErrorResponse> handleFlywayException(final FlywayException ex) {
    log.error("[FlywayException] {}", ex.getMessage());
    return ResponseEntity
        .status(CommonErrorCode.FLYWAY_MIGRATION_ERROR.getStatus())
        .body(ErrorResponse.of(CommonErrorCode.FLYWAY_MIGRATION_ERROR, ex.getMessage()));
  }

  // 일반 예외 (Fallback)
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
    log.error("[UnhandledException]", ex);
    return ResponseEntity
        .status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(CommonErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
  }
}
