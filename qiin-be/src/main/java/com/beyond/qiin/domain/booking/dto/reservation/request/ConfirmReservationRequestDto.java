package com.beyond.qiin.domain.booking.dto.reservation.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ConfirmReservationRequestDto {

    @NotNull
    private Long version;

    private String reason;
}
