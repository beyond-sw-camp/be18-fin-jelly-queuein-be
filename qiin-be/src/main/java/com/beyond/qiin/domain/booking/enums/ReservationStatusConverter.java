package com.beyond.qiin.domain.booking.enums;

import com.beyond.qiin.common.enums.EnumCodeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ReservationStatusConverter extends EnumCodeConverter<ReservationStatus> {
    public ReservationStatusConverter() {
        super(ReservationStatus.class);
    }
}
