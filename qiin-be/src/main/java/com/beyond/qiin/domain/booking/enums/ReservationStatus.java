package com.beyond.qiin.domain.booking.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus implements EnumCode {
    PENDING(0),
    APPROVED(1),
    USING(2),
    REJECTED(3),
    CANCELED(4),
    COMPLETED(5),
    UNAVAILABLE(6); //자원 사용 불가 시

    private final int code;

    private static final Map<Integer, ReservationStatus> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(ReservationStatus::getCode, s -> s));

    public static ReservationStatus from(int code) {
        ReservationStatus status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Invalid ReservationStatus code: " + code);
        }
        return status;
    }
}
