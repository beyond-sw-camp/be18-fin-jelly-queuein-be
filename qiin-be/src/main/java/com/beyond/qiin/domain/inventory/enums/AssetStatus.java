package com.beyond.qiin.domain.inventory.enums;

import com.beyond.qiin.common.enums.EnumCode;
import com.beyond.qiin.domain.inventory.exception.AssetException;
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

    private static final Map<String, AssetStatus> NAME_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Enum::name, t -> t));

    // int → enum
    public static AssetStatus fromCode(final int code) {
        AssetStatus status = CODE_MAP.get(code);
        if (status == null) {
            throw AssetException.invalidStatus();
        }
        return status;
    }

    // String → enum
    public static AssetStatus fromName(String name) {
        AssetStatus status = NAME_MAP.get(name);
        if (status == null) {
            throw AssetException.invalidStatus();
        }
        return status;
    }

    // enum → int
    public int toCode() {
        return this.code;
    }

    // enum → String
    public String toName() {
        return this.name();
    }
}
