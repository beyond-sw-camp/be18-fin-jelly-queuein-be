package com.beyond.qiin.domain.inventory.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetStatus implements EnumCode {
    AVAILABLE(0),
    UNAVAILABLE(1),
    MAINTENANCE(2);

    private final int code;

    private static final Map<Integer, AssetStatus> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(AssetStatus::getCode, t -> t));

    public static AssetStatus from(final int code) {
        AssetStatus status = CODE_MAP.get(code);
        if (status == null) {
            throw new IllegalArgumentException("Invalid AssetStatus code: " + code);
        }
        return status;
    }
}
