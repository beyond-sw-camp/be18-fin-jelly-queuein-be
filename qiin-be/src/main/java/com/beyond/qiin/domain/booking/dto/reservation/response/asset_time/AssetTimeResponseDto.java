package com.beyond.qiin.domain.booking.dto.reservation.response.asset_time;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AssetTimeResponseDto {

    private final Long assetId;

    @Builder.Default
    private final List<TimeSlotDto> timeSlots = new ArrayList<>();

    public static AssetTimeResponseDto create(final Long assetId, final List<TimeSlotDto> timeSlots) {
        return AssetTimeResponseDto.builder()
                .assetId(assetId)
                .timeSlots(timeSlots)
                .build();
    }
}
