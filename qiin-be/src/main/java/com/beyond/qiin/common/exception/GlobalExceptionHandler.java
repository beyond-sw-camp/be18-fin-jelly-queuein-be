package com.beyond.qiin.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.FlywayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(final BaseException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("[BusinessException] {} - {}", code.getError(), ex.getMessage());
        return ResponseEntity.status(code.getStatus()).body(ErrorResponseDto.of(code, ex.getMessage()));
    }

    // Flyway 마이그레이션 예외 (DB 버전 충돌, 스크립트 오류 등)
    @ExceptionHandler(FlywayException.class)
    public ResponseEntity<ErrorResponseDto> handleFlywayException(final FlywayException ex) {
        log.error("[FlywayException] {}", ex.getMessage());
        return ResponseEntity.status(CommonErrorCode.FLYWAY_MIGRATION_ERROR.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.FLYWAY_MIGRATION_ERROR, ex.getMessage()));
    }

    // 일반 예외 (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(final Exception ex) {
        log.error("[UnhandledException]", ex);
        return ResponseEntity.status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }

    // 403 에러
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException ex) {

        log.warn("[AuthorizationDeniedException] {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .status(403)
                        .error("FORBIDDEN")
                        .message("접근 권한이 없습니다.")
                        .build());
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex) {

        log.warn("[AccessDeniedException] {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponseDto.builder()
                        .status(403)
                        .error("FORBIDDEN")
                        .message("접근 권한이 없습니다.")
                        .build());
    }
}
