package com.beyond.qiin.domain.booking.dto.reservation.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UpdateReservationRequestDto {

    // update 충돌 방지용 (create용과 무관)
    @NotNull
    private Long version;

    private String description;

    private List<Long> attendantIds;
}
