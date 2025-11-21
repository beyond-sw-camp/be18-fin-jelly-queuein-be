package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.attendant.entity.Attendant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AttendantResponseDto {

    private final Long attendantId;
    private final String attendantName;

    public static AttendantResponseDto fromEntity(Attendant attendant){
        return AttendantResponseDto.builder()
            .attendantId(attendant.getId())
            .attendantName(attendant.getUser().getUserName())
            .build();
    }
}
