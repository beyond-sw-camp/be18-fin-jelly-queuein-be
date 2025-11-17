package com.beyond.qiin.domain.booking.dto.reservation.response;

import com.beyond.qiin.domain.booking.reservation.vo.TimeSlot;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ReservableAssetTimeResponseDto {

  private final Long assetId;

  @Builder.Default
  private final List<TimeSlotDto> timeSlots = new ArrayList<>();

  public static ReservableAssetTimeResponseDto create(Long assetId, List<TimeSlotDto> timeSlots){
    return ReservableAssetTimeResponseDto.builder()
        .assetId(assetId)
        .timeSlots(timeSlots)
        .build();
  }
}
