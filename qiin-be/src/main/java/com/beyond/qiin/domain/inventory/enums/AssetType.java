package com.beyond.qiin.domain.inventory.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.beyond.qiin.domain.inventory.exception.AssetException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetType implements EnumCode {
    STATIC(0),
    DYNAMIC(1);

    private final int code;

    public static final Map<Integer, AssetType> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(AssetType::getCode, t -> t));

    private static final Map<String, AssetType> NAME_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(Enum::name, t -> t));

    // int → enum
    public static AssetType fromCode(int code) {
        AssetType assetType = CODE_MAP.get(code);
        if (assetType == null) {
            throw AssetException.invalidType();
        }
        return assetType;
    }

    // String → enum
    public static AssetType fromName(String name) {
        AssetType type = NAME_MAP.get(name);
        if (type == null) {
            throw AssetException.invalidType();
        }
        return type;
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
