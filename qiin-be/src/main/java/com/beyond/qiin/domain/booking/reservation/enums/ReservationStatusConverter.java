package com.beyond.qiin.domain.booking.reservation.enums;

import com.beyond.qiin.common.enums.EnumCodeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReservationStatusConverter extends EnumCodeConverter<AssetStatus> {
  public AssetStatusConverter() {
    super(AssetStatus.class);
  }
}
