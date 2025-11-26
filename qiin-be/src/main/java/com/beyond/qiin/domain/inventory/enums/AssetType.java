package com.beyond.qiin.domain.inventory.enums;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
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

    public static AssetType from(int code) {
        AssetType assetType = CODE_MAP.get(code);
        if (assetType == null) {
            throw new IllegalArgumentException("Invalid AssetType code: " + code);
        }
        return assetType;
    }
}
