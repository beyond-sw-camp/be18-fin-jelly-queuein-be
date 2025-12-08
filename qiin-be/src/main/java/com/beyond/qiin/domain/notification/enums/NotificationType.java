package com.beyond.qiin.domain.notification.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType implements EnumCode {
    RESERVATION_APPROVED(0),
    RESERVATION_REJECTED(1),
    RESERVATION_PLANNED(2),
    RESERVATION_INVITED(3),
    RESERVATION_CREATED(4),
    RESERVATION_UNAVAILABLE(5);

    private final int code;

    private static final Map<Integer, NotificationType> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(NotificationType::getCode, type -> type));

    public static NotificationType from(int code) {
        NotificationType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("Invalid NotificationType code: " + code);
        }
        return type;
    }
}
