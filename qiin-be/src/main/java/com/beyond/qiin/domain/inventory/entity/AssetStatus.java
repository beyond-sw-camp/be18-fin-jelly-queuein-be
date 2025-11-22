package com.beyond.qiin.domain.inventory.entity;

import com.beyond.qiin.common.enums.EnumCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetStatus implements EnumCode {
  AVAILABLE(0),
  UNAVAILABLE(1),
  MAINTENANCE(2);

  private final int code;

  public static AssetStatus from(int code) {
    return Arrays.stream(values())
        .filter(v -> v.code == code)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
  }

}
