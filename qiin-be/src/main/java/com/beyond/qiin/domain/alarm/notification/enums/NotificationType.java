package com.beyond.qiin.domain.alarm.notification.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType implements EnumCode {
    // 생성
    RESERVATION_CREATED(4, "예약이 생성되었습니다. 시간: %s ~ %s"),

    RESERVATION_INVITED(3, "예약에 초대되었습니다. 시간: %s ~ %s"), // 별도 notification event
    // update
    RESERVATION_APPROVED(0, "예약이 승인되었습니다. 시간: %s ~ %s"),
    RESERVATION_REJECTED(1, "예약이 거절되었습니다. 시간: %s ~ %s"),
    RESERVATION_UNAVAILABLE(5, "자원이 사용 불가능 상태가 되었습니다."), // 별도 kafka event

    // reminder
    RESERVATION_PLANNED(2, "예약이 확정되었습니다. 시간: %s ~ %s");

    private final int code;
    private final String messageTemplate;

    private static final Map<Integer, NotificationType> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(NotificationType::getCode, type -> type));

    public static NotificationType from(int code) {
        NotificationType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid NotificationType code: " + code);
        }
        return type;
    }

    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
