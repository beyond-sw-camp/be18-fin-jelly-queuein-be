package com.beyond.qiin.domain.inventory.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class CategoryException extends BaseException {

    // 기본 생성자 (공통 에러 메시지 사용)
    public CategoryException(ErrorCode errorCode) {
        super(errorCode);
    }

    // 세부 메시지를 직접 지정하고 싶을 때 사용
    public CategoryException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // 카테고리 에러 정의

    public static CategoryException notFound() {
        return new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND);
    }

    public static CategoryException duplicateName() {
        return new CategoryException(CategoryErrorCode.CATEGORY_DUPLICATE_NAME);
    }

    // ErrorCode 내부 정의
    @Getter
    public enum CategoryErrorCode implements ErrorCode {
        CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY_NOT_FOUND", "해당 카테고리를 찾을 수 없습니다."),
        CATEGORY_DUPLICATE_NAME(HttpStatus.CONFLICT, "CATEGORY_DUPLICATE_NAME", "이미 존재하는 카테고리입니다."),
        ;
        private final HttpStatus status;
        private final String error;
        private final String message;

        CategoryErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
