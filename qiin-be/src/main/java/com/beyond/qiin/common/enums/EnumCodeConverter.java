package com.beyond.qiin.common.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;

@Converter(autoApply = true)
public class EnumCodeConverter <T extends Enum<T> & EnumCode>
    implements AttributeConverter<T, Integer> {
  private final Class<T> enumClass;

  public EnumCodeConverter(Class<T> enumClass) {
    this.enumClass = enumClass;
  }

  @Override
  public Integer convertToDatabaseColumn(T attribute) {
    return attribute.getCode();
  }

  @Override
  public T convertToEntityAttribute(Integer dbData) {
    return Arrays.stream(enumClass.getEnumConstants())
        .filter(e -> e.getCode() == dbData)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + dbData));
  }

}
