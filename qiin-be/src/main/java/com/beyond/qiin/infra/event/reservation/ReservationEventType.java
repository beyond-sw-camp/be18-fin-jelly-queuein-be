package com.beyond.qiin.infra.event.reservation;

import com.beyond.qiin.common.enums.EnumCode;
import com.beyond.qiin.domain.notification.exception.NotificationErrorCode;
import com.beyond.qiin.domain.notification.exception.NotificationException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

// reservation status(결과)의 trigger이 되는 이벤트(원인)
// 10분전 알림은 별도 처리, invite : 알림 정책이므로 notification type으로 넣음
@Getter
@RequiredArgsConstructor
public enum ReservationEventType implements EnumCode {
    CREATED(0),
    UNAVAILABLE(1),
    REJECTED(2),
    APPROVED(3);

    private final int code;

    private static final Map<Integer, ReservationEventType> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(ReservationEventType::getCode, t -> t));

    private static final Map<String, ReservationEventType> NAME_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Enum::name, t -> t));

    public static ReservationEventType fromCode(int code) {
        ReservationEventType type = CODE_MAP.get(code);
        if (type == null) {
            throw new NotificationException(
                    NotificationErrorCode.NOTIFICATION_NOT_FOUND, "Unknown ReservationEventType code: " + code);
        }
        return type;
    }

    public static ReservationEventType fromName(String name) {
        ReservationEventType type = NAME_MAP.get(name);
        if (type == null) {
            throw new NotificationException(
                    NotificationErrorCode.NOTIFICATION_NOT_FOUND, "Unknown ReservationEventType name: " + name);
        }
        return type;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    public String toName() {
        return this.name();
    }
}
