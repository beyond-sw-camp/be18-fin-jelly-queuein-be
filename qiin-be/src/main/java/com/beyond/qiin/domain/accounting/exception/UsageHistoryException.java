package com.beyond.qiin.domain.accounting.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class UsageHistoryException extends BaseException {

    private UsageHistoryException(final UsageHistoryErrorCode code) {
        super(code);
    }

    public static UsageHistoryException notFound() {
        return new UsageHistoryException(UsageHistoryErrorCode.USAGE_HISTORY_NOT_FOUND);
    }

    public static UsageHistoryException invalidAssetId() {
        return new UsageHistoryException(UsageHistoryErrorCode.INVALID_ASSET_ID);
    }

    public static UsageHistoryException invalidAssetName() {
        return new UsageHistoryException(UsageHistoryErrorCode.INVALID_ASSET_NAME);
    }

    @Getter
    public enum UsageHistoryErrorCode implements ErrorCode {
        USAGE_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "USAGE_HISTORY_NOT_FOUND", "해당 사용 이력을 찾을 수 없습니다."),

        INVALID_ASSET_ID(HttpStatus.BAD_REQUEST, "INVALID_ASSET_ID", "존재하지 않는 자원 ID입니다."),

        INVALID_ASSET_NAME(HttpStatus.BAD_REQUEST, "INVALID_ASSET_NAME", "존재하지 않는 자원명입니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        UsageHistoryErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
