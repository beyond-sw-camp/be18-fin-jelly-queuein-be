package com.beyond.qiin.domain.alarm.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationStatus implements EnumCode {
    PENDING(0), // 알림 생성됨 (전송 전)
    SENT(1), // SSE로 전송 완료
    FAILED(2); // 전송 실패 (재시도 필요)

    private final int code;

    private static final Map<Integer, NotificationStatus> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(NotificationStatus::getCode, status -> status));

    public static NotificationStatus from(int code) {
        NotificationStatus status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Invalid NotificationStatus code: " + code);
        }
        return status;
    }
}
