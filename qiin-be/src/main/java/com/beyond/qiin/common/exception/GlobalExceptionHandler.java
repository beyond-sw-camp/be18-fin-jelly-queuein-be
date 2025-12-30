package com.beyond.qiin.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.FlywayException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseException(final BaseException ex) {
        ErrorCode code = ex.getErrorCode();
        log.warn("[BusinessException] {} - {}", code.getError(), ex.getMessage());
        return ResponseEntity.status(code.getStatus()).body(ErrorResponseDto.of(code, ex.getMessage()));
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            final org.springframework.web.bind.MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(CommonErrorCode.BAD_REQUEST.getMessage());

        log.warn("[ValidationException] {}", message);

        return ResponseEntity.status(CommonErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.BAD_REQUEST, message));
    }

    @ExceptionHandler(org.springframework.validation.BindException.class)
    public ResponseEntity<ErrorResponseDto> handleBindException(final org.springframework.validation.BindException ex) {

        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse(CommonErrorCode.BAD_REQUEST.getMessage());

        log.warn("[BindException] {}", message);

        return ResponseEntity.status(CommonErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.BAD_REQUEST, message));
    }

    // Flyway 마이그레이션 예외 (DB 버전 충돌, 스크립트 오류 등)
    @ExceptionHandler(FlywayException.class)
    public ResponseEntity<ErrorResponseDto> handleFlywayException(final FlywayException ex) {
        log.error("[FlywayException] {}", ex.getMessage());
        return ResponseEntity.status(CommonErrorCode.FLYWAY_MIGRATION_ERROR.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.FLYWAY_MIGRATION_ERROR, ex.getMessage()));
    }

    // 403 에러
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAuthorizationDeniedException(
            org.springframework.security.authorization.AuthorizationDeniedException ex) {

        log.warn("[AuthorizationDeniedException] {}", ex.getMessage());

        return ResponseEntity.status(CommonErrorCode.FORBIDDEN.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.FORBIDDEN, null));
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            final org.springframework.security.access.AccessDeniedException ex) {

        log.warn("[AccessDeniedException] {}", ex.getMessage());

        return ResponseEntity.status(CommonErrorCode.FORBIDDEN.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.FORBIDDEN, null));
    }

    // 일반 예외 (Fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(final Exception ex) {
        log.error("[UnhandledException]", ex);
        return ResponseEntity.status(CommonErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponseDto.of(CommonErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage()));
    }
}
