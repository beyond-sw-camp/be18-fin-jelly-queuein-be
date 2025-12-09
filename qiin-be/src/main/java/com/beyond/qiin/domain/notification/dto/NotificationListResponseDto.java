package com.beyond.qiin.domain.notification.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NotificationListResponseDto {
    @Default
    List<NotificationResponseDto> notifications = new ArrayList<>();
}
