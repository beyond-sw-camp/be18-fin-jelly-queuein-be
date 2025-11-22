package com.beyond.qiin.domain.inventory.entity;

import com.beyond.qiin.common.enums.EnumCodeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AssetStatusConverter extends EnumCodeConverter<AssetStatus> {
    public AssetStatusConverter() {
        super(AssetStatus.class);
    }
}
