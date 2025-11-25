package com.beyond.qiin.domain.booking.dto.reservation.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 아직 사용 x
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReservationListResponseDto {

    @Builder.Default
    private final List<ReservationDetailResponseDto> reservationList = new ArrayList<>();
}
