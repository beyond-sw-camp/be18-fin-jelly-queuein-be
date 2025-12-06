package com.beyond.qiin.domain.booking.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WaitingQueueStatus {
    WAITING(0), // 대기 중
    ACTIVE(1); // 활성 상태

    private final int code;

    // code -> enum
    private static final Map<Integer, WaitingQueueStatus> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(WaitingQueueStatus::getCode, s -> s));

    // int code -> enum
    public static WaitingQueueStatus from(int code) {
        WaitingQueueStatus status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Invalid WaitingQueueStatus code: " + code);
        }
        return status;
    }
}
