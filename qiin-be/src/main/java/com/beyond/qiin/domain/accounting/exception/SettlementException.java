package com.beyond.qiin.domain.accounting.exception;

import com.beyond.qiin.common.exception.BaseException;
import com.beyond.qiin.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class SettlementException extends BaseException {

    private SettlementException(final SettlementErrorCode code) {
        super(code);
    }

    public static SettlementException notFound() {
        return new SettlementException(SettlementErrorCode.SETTLEMENT_NOT_FOUND);
    }

    @Getter
    public enum SettlementErrorCode implements ErrorCode {
        SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "SETTLEMENT_NOT_FOUND", "해당 정산 정보를 찾을 수 없습니다.");

        private final HttpStatus status;
        private final String error;
        private final String message;

        SettlementErrorCode(final HttpStatus status, final String error, final String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
    }
}
