package com.beyond.qiin.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final int status; // HttpStatus -> int / ENUM 값 직렬화
    private final String error; // e.g. NOT_FOUND
    private final String message; // e.g. "요청한 리소스를 찾을 수 없습니다."

    // 표준 예외처리 응답
    public static ErrorResponse of(final ErrorCode errorCode, final String message) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus().value())
                .error(errorCode.getError().toUpperCase())
                .message(message != null ? message : errorCode.getMessage())
                .build();
    }
}
