package com.beyond.qiin.common.exception;

import org.springframework.http.HttpStatus;

// 예외 처리 공통 인터페이스
public interface ErrorCode {
    HttpStatus getStatus();

    String getError();

    String getMessage();
}
