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

    //예약 시간 변경하는 경우
//    @NotNull
//    private Long version;

    private String description;

    private List<Long> attendantIds;
}
