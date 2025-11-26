package com.beyond.qiin.domain.inventory.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AssetException extends BaseException {

    // 기본 생성자 (공통 에러 메시지 사용)
    public AssetException(ErrorCode errorCode) {
        super(errorCode);
    }

    // 세부 메시지를 직접 지정하고 싶을 때 사용
    public AssetException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    // 자원 에러 정의

    public static AssetException notFound() {
        return new AssetException(AssetErrorCode.ASSET_NOT_FOUND);
    }

    public static AssetException duplicateName() {
        return new AssetException(AssetErrorCode.ASSET_DUPLICATE_NAME);
    }

    public static AssetException cannotMoveToChild() {
        return new AssetException((AssetErrorCode.ASSET_CANNOT_MOVE_TO_CHILD));
    }

    public static AssetException assetNotAvailable() {
        return new AssetException((AssetErrorCode.ASSET_NOT_AVAILABLE));
    }

    // ErrorCode 내부 정의
    @Getter
    public enum AssetErrorCode implements ErrorCode {
        ASSET_NOT_FOUND(HttpStatus.NOT_FOUND, "ASSET_NOT_FOUND", "해당 자원를 찾을 수 없습니다."),
        ASSET_DUPLICATE_NAME(HttpStatus.CONFLICT, "ASSET_DUPLICATE_NAME", "이미 존재하는 자원입니다."),
        ASSET_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "ASSET_NOT_AVAILABLE", "자원이 사용 가능 상태가 아닙니다."),
        ASSET_HAS_CHILDREN(HttpStatus.BAD_REQUEST,"ASSET_HAS_CHILDREN","해당 자원에 자식 자원이 있어 삭제할 수 없습니다."),
        ASSET_PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ASSET_PARENT_NOT_FOUND", "부모 자원을 찾을 수 없습니다."),

        ASSET_CANNOT_MOVE_TO_CHILD(HttpStatus.BAD_REQUEST, "ASSET_CANNOT_MOVE_TO_CHILD", "자식 자원으로 이동할 수 없습니다."),
        ASSET_CANNOT_MOVE_TO_SELF(HttpStatus.BAD_REQUEST, "ASSET_CANNOT_MOVE_TO_SELF", "자원은 자기 자신 아래로 이동할 수 없습니다."),
        ASSET_ALREADY_ROOT(HttpStatus.BAD_REQUEST, "ASSET_ALREADY_ROOT", "이미 루트 자원입니다."),


        ;

        private final HttpStatus status;
        private final String error;
        private final String message;

        AssetErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
